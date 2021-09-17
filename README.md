# Ejercicio lógico
El ejercicio consiste en calcular el año en el cual más personas estuvieron vivas dada una lista de las fechas de nacimiento y defunción de un grupo de personas.

## Requisitos para su ejecución
1. Java JDK en su versión 8 o superior.
2. Un data de entrada en formato JSON, en el proyecto se incluye uno de ejemplo. Si se desea utilizar otro data considerar lo siguiente:
   1. Deberá contener un key raíz llamado "data"
   2. Esta key contendrá un arreglo de objetos
   3. Cada objeto deberá contener dos keys "birthYear" y "deathYear" y el valor debe ser númerico.
   4. Se deberá sustituir el data de ejemplo ubicado en la ruta src/main/resources

## Explicación de la lógica en el programa
El cálculo se realiza basandose en lo siguiente:
1. Se genera un arreglo para contener los años de los nacimientos, y otro para las defunciones.
2. Con los arreglos por medio de la clase Collection, se genera un TreeMap por cada arreglo, en donde las keys será el año y sus values será la frecuencia con la que se repite ese año en el arreglo.
3. Con los TreeMap se procede a iterarlos, para generar un nuevo TreeMap, en el cual las keys será el año, y los values será el acumulado de nacimientos o defunciones hasta ese año.
4. Posteriormente con últimos TreeMaps se genera un HasMap, el cual contendrá como keys el año, y los values será la diferencia del acumulado de nacimientos en ese año y el acumulado de defunciones en el año anterior, ya que las defunciones ocurridas en ese año son excluidas en el conteo para la cantidad de personas vivas.
5. Finalmente se obtiene el mayor value en el HashMap que contiene las personas vivas por año, y se buscan las keys que contengan ese valor, se retorna el arreglo de años, en caso de ser mas de uno, que tienen la mayor cantidad de personas vivas.