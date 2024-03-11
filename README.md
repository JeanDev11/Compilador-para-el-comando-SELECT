# Compilador-para-el-comando-SELECT
Este proyecto es un compilador que permite el reconocimiento y análisis del comando "SELECT" en el lenguaje Java. El compilador es capaz de identificar y procesar las distintas partes de una sentencia SELECT, tales como: "FROM", "WHERE", "ORDER BY", "MAX", "AVG", "LEFT," etc.; así como también validar su estructura y semántica.

CARACTERÍSTICAS PRINCIPALES:
- Análisis Léxico: El compilador cuenta con un analizador léxico que segmenta la sentencia SQL en tokens, identificando palabras clave como "SELECT", "FROM", "WHERE", "ORDER BY", y otros elementos como identificadores, literales y operadores.
- Análisis Sintáctico: Utilizando técnicas de análisis sintáctico, el compilador verifica la estructura gramatical de la sentencia SELECT, asegurando que cumpla con las reglas sintácticas definidas para el lenguaje SQL, con leves adaptaciones propias.
- Análisis Semántico: Una vez validada la estructura sintáctica, el compilador realiza un análisis semántico para verificar la coherencia y corrección de la sentencia SELECT. Esto incluye la verificación de la existencia y consistencia de tablas y columnas mencionadas, así como también la correcta aplicación de funciones y operaciones.
- Soporte para Funcionalidades Avanzadas: El compilador es capaz de reconocer y procesar diversas funcionalidades avanzadas del lenguaje SQL, como agregaciones (MAX, MIN, AVG), funciones de agrupación, condicones, ordenamiento, entre otros.

USO: Para usar el compilador, simplemente proporciona una sentencia SELECT como entrada. A continuación, el compilador analizará el código fuente y proporcionará resultados sobre el comando "SELECT" y otros elementos reconocidos, devolviendo los regristros solicitados.

NOTA: Los registros se obtienen de los archivos CATALOG.DAT y SM.DAT, los cuales son fundamentales para el funcionamiento del compilador. El primero contiene la estructura de cada tabla, incluyendo los campos y sus tipos de datos. Por otro lado, SM.DAT almacena los registros correspondientes a cada tabla mencionada en CATALOG.DAT.

LICENCIA: Este proyecto está licenciado bajo Apache 2.0 License, lo que significa que puedes utilizarlo de acuerdo con los términos de dicha licencia.
