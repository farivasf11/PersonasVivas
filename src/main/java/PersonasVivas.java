
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovyjarjarantlr4.runtime.tree.Tree;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class PersonasVivas {

    private List<Integer> añosNacimientos ;
    private List<Integer> añosDefunciones ;
    private TreeMap<Integer, Integer> acumuladoDefunciones = new TreeMap<>();
    private TreeMap<Integer, Integer> acumuladoNacimientos = new TreeMap<>();

    private void CargarDatos(){
        try {
            URL path = this.getClass().getResource("/data.json");
            File file = new File(path.getFile());
            HashMap<String,Object> jsonMap = new ObjectMapper().readValue(file, HashMap.class);
            ArrayList<HashMap<String, Integer>> data = (ArrayList<HashMap<String, Integer>>) jsonMap.get("data");
            añosNacimientos = data.stream().map(p -> p.get("birthYear")).collect(Collectors.toList());
            añosDefunciones = data.stream().map(p -> p.get("deathYear")).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void AcumularPorAño(){
        /*
            Se genera un TreeMap para los nacimientos y defunciones, en donde a cada año se le asigna su frecuencia con la que se repite en el arreglo de años.
            El TreeMap nos ayudará a tener los años ordenados de menor a mayor, así como métodos que permitan obtener un lowerKey dado un key proporcionado.
         */
        TreeMap<Integer, Long> nacimientosPorAño = new TreeMap<>(añosNacimientos.stream().collect(groupingBy(p -> p , Collectors.counting())));
        TreeMap<Integer, Long> defuncionesPorAño = new TreeMap<>(añosDefunciones.stream().collect(groupingBy(p -> p , Collectors.counting())));

        //Con las frecuencias por año para cada uno, se procede a acumular los nacimientos y defunciones, de mayor a menor, para posteriormente poder calcular la cantidad de personas vivas dado un año.
        defuncionesPorAño.forEach( (integer, aLong) ->
                acumuladoDefunciones.put(
                    integer,
                    defuncionesPorAño.subMap(defuncionesPorAño.firstKey(), integer + 1).values().stream().mapToInt(d-> Math.toIntExact(d)).sum()
                ));

        nacimientosPorAño.forEach( (integer, aLong) ->
                acumuladoNacimientos.put(
                    integer,
                    nacimientosPorAño.subMap(nacimientosPorAño.firstKey(), integer + 1).values().stream().mapToInt(d-> Math.toIntExact(d)).sum()
                ));
    }

    private void CalcularPersonasVivasPorAño(){
        //Con el acumulado de nacimientos y defunciones por año, es posible calcular el número de personas vivas en un año.
        //El calculo es el siguiente, el número de personas vivas en un año es igual a la diferencia entre el acumulado de nacimientos y el acumulado de defunciones del año anterior
        Map<Integer, Integer> personasVivasPorAño = new TreeMap<>();
        acumuladoNacimientos.forEach((integer, aLong) -> {
            if (acumuladoDefunciones.lowerKey(integer) != null) {
                personasVivasPorAño.put(integer, (aLong - acumuladoDefunciones.lowerEntry(integer).getValue()));
            } else {
                personasVivasPorAño.put(integer, aLong);
            }
        });

        System.out.println(" - Acumulado de nacimientos por año: \n" + acumuladoNacimientos);
        System.out.println(" - Acumulado de defunciones por año: \n" + acumuladoDefunciones);
        System.out.println("----------Resultados----------\n - Personas vivas por año:");
        System.out.println(personasVivasPorAño);

        int maximaCantidad = Collections.max(personasVivasPorAño.values());
        List<Integer> añosMayorCantidad = personasVivasPorAño.entrySet().stream()
                .filter(entry -> entry.getValue() == maximaCantidad)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        System.out.println("Máxima cantidad de personas vivas en un año: " + maximaCantidad);
        System.out.println("Años con mayor cantidad de  personas vivas: " + añosMayorCantidad);
    }

    public static void main (String args[]){
        PersonasVivas p = new PersonasVivas();
        p.CargarDatos();
        p.AcumularPorAño();
        p.CalcularPersonasVivasPorAño();
    }
}