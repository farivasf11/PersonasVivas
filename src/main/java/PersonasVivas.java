
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class PersonasVivas {

    List<Integer> añosNacimientos ;
    List<Integer> añosDefunciones ;


    public void CargarDatos(){
        InputStream getLocalJsonFile = null;
        try {
            URL path = this.getClass().getResource("/data.json");
            File file = new File(path.getFile());


            HashMap<String,Object> jsonMap = new ObjectMapper().readValue(file, HashMap.class);

            ArrayList<HashMap<String, Integer>> data = (ArrayList<HashMap<String, Integer>>) jsonMap.get("data");

            System.out.println(data.get(0).get("birthYear"));
            System.out.println();
            añosNacimientos = data.stream().map(stringIntegerHashMap -> stringIntegerHashMap.get("birthYear")).collect(Collectors.toList());
            añosDefunciones = data.stream().map(stringIntegerHashMap -> stringIntegerHashMap.get("deathYear")).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void Calcular(){

        //List<Integer> añosNacimientos = Arrays.asList(1929, 1929, 1930, 1931, 1945, 1934, 1954, 1956, 1953, 1954, 1919, 1929, 1929);
        //List<Integer> añosDefunciones = Arrays.asList(1980, 1935, 1933, 1932, 1990, 1954, 1994, 1966, 1993, 1984, 1969, 1939, 1969);

        TreeMap<Integer, Long> frecuenciaPorAño = new TreeMap<>(añosNacimientos.stream().collect(groupingBy(p -> p , Collectors.counting())));
        TreeMap<Integer, Long> frecuenciaPorAñoDef = new TreeMap<>(añosDefunciones.stream().collect(groupingBy(p -> p , Collectors.counting())));


        TreeMap<Integer, Integer> newMapDef = new TreeMap<>();
        frecuenciaPorAñoDef.forEach( (integer, aLong) ->
                newMapDef.put(integer,
                        frecuenciaPorAñoDef.subMap(frecuenciaPorAñoDef.firstKey(), integer + 1).values().stream().mapToInt(d-> Math.toIntExact(d)).sum()
                ));


        Map<Integer, Integer> newMap = new TreeMap<>();
        frecuenciaPorAño.forEach( (integer, aLong) ->
                newMap.put(integer,
                        frecuenciaPorAño.subMap(frecuenciaPorAño.firstKey(), integer + 1).values().stream().mapToInt(d-> Math.toIntExact(d)).sum()
                ));

        Map<Integer, Integer> personasVivasPorAño = new TreeMap<>();
        newMap.forEach((integer, aLong) -> {
            if (newMapDef.floorKey(integer) != null) {
                System.out.println(integer + " : "  + (aLong - newMapDef.floorEntry(integer).getValue()));
                System.out.println("ValueBirths: " + aLong);
                System.out.println("ValueDeaths: " + newMapDef.floorEntry(integer).getValue());
                personasVivasPorAño.put(integer, (aLong - newMapDef.floorEntry(integer).getValue()));
            } else {
                System.out.println(integer + " : "  + aLong);
                System.out.println("ValueBirths: " + aLong);
                personasVivasPorAño.put(integer, aLong);
            }
            System.out.println("---------------------------");
        });


        System.out.println(newMap);
        System.out.println(newMapDef);
        System.out.println("-----Resultados-----");
        System.out.println(personasVivasPorAño);
        int max = Collections.max(personasVivasPorAño.values());
        List<Integer> keys = personasVivasPorAño.entrySet().stream()
                .filter(entry -> entry.getValue() == max)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        System.out.println("Máximas personas vivas: " + max);
        System.out.println("Años con mayores personas vivas: " + keys);
    }
    public PersonasVivas(){
    }

    public static void main (String args[]){
        PersonasVivas p = new PersonasVivas();
        p.CargarDatos();
        p.Calcular();
    }
}