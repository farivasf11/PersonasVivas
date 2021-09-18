import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class PersonasVivasV2 {

    private List<Integer> añosNacimientos ;
    private List<Integer> añosDefunciones ;
    private void CargarDatos(){
        try {
            URL path = this.getClass().getResource("/data.json");
            File file = new File(path.getFile());
            HashMap<String,Object> jsonMap = new ObjectMapper().readValue(file, HashMap.class);
            ArrayList<HashMap<String, Integer>> data = (ArrayList<HashMap<String, Integer>>) jsonMap.get("data");
            añosNacimientos = data.stream().map(p -> p.get("birthYear")).collect(Collectors.toList());
            añosDefunciones = data.stream().map(p -> p.get("deathYear")).collect(Collectors.toList());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CalcularPersonasVivas(){
        //Se genera un TreeMap para los nacimientos y defunciones, en donde a cada año se le asigna su frecuencia con la que se repite en el arreglo de años.
        //El TreeMap nos ayudará a tener los años ordenados de menor a mayor, así como métodos que permitan obtener un lowerKey dado un key proporcionado.
        TreeMap<Integer, Long> nacimientosPorAño = new TreeMap<>(añosNacimientos.stream().collect(groupingBy(p -> p , Collectors.counting())));
        TreeMap<Integer, Long> defuncionesPorAño = new TreeMap<>(añosDefunciones.stream().collect(groupingBy(p -> p , Collectors.counting())));
        //El cálculo de personas vivas por año será igual a la diferencia del acumulado de los nacimientos menos el acumulado de las defunciones del año anterior.
        Map<Integer, Integer> personasVivasPorAño = new TreeMap<>();
        nacimientosPorAño.forEach((anio, valor) -> {
            if (defuncionesPorAño.lowerKey(anio) != null) {
                int personasVivas =
                        nacimientosPorAño.subMap(nacimientosPorAño.firstKey(), true, anio, true).values().stream().mapToInt(value -> Math.toIntExact(value)).sum()
                        - defuncionesPorAño.subMap(defuncionesPorAño.firstKey(), anio).values().stream().mapToInt(value -> Math.toIntExact(value)).sum();
                personasVivasPorAño.put(anio,personasVivas);
            } else {
                personasVivasPorAño.put(anio, Math.toIntExact(valor));
            }
        });

        System.out.println("----------Resultados----------\n - Personas vivas por año:\n" + personasVivasPorAño);
        int maximaCantidad = Collections.max(personasVivasPorAño.values());
        List<Integer> añosMayorCantidad = personasVivasPorAño.entrySet().stream()
                .filter(entry -> entry.getValue() == maximaCantidad)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        System.out.println("Máxima cantidad de personas vivas en un año: " + maximaCantidad);
        System.out.println("Años con mayor cantidad de  personas vivas: " + añosMayorCantidad);
    }

    public static void main (String args[]){
        PersonasVivasV2 p = new PersonasVivasV2();
        p.CargarDatos();
        p.CalcularPersonasVivas();
    }
}