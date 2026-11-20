-- =====================================================================
-- V8 - Alinear contenidos, actividades y pruebas finales con MPAZ_PRO_6
-- =====================================================================
--
-- Fuente de verdad: seed final de la app mobile (MpazDatabase.kt, Room v9),
-- es decir, la suma de seedInitialData + seedMathData + seedHistoryData +
-- seedFinalExamsLenguajeCiencias.
--
-- La migracion es destructiva SOLO sobre el dominio pedagogico/evaluativo:
-- elimina contenidos, recursos, actividades, asignaciones actividad-curso,
-- preguntas, alternativas y sus vinculos, junto con los intentos y respuestas
-- de ejemplo (dependen de esas actividades y preguntas). Luego reconstruye
-- todo con los ids y textos del mobile.
--
-- No modifica usuarios, roles, cursos, unidades, objetivos de aprendizaje ni
-- informes pedagogicos: el catalogo de OA del backend se conserva y solo se
-- recrean sus vinculos con los nuevos contenidos.
--
-- Estado final esperado:
--   6 contenidos (Lenguaje 1-2, Ciencias 3-4, Matematica 5, Historia 6)
--   12 actividades (8 ACTIVIDAD + 4 PRUEBA_FINAL)
--   24 asignaciones actividad-curso (12 actividades x 2 cursos)
--   48 preguntas y 192 alternativas
--   60 vinculos pregunta-actividad y 192 vinculos alternativa-pregunta
-- =====================================================================

-- 1. Limpieza en orden seguro para las claves foraneas.
DELETE FROM respuesta;
DELETE FROM intento;
DELETE FROM alternativa_pregunta;
DELETE FROM pregunta_actividad;
DELETE FROM alternativa;
DELETE FROM pregunta;
DELETE FROM actividad_curso;
DELETE FROM actividad;
DELETE FROM recurso;
DELETE FROM objetivo_aprendizaje_contenido;
DELETE FROM contenido;

-- 2. Contenidos (estado PUBLICADO = 4, igual que el mobile).
INSERT INTO contenido (contenido_id, descripcion, estado_id, unidad_id) VALUES
    (1, $v8$# Estrategias para comprender lo que leemos

## ¿Cómo podemos comprender mejor un texto?

Leer es mucho más que reconocer palabras y pronunciarlas correctamente. Cuando leemos, nuestra mente trabaja para comprender las ideas, relacionar información y construir significados.

Cada texto nos entrega información que podemos conectar con lo que ya sabemos, con nuestras experiencias y con otros conocimientos que hemos adquirido.

Para comprender mejor lo que leemos, podemos utilizar distintas estrategias antes, durante y después de la lectura.

## 1. Antes de leer: activar nuestros conocimientos

Antes de comenzar, podemos observar el título, las imágenes y otros elementos que acompañan al texto.

Esto nos permite anticipar de qué podría tratarse la lectura y recordar lo que sabemos sobre ese tema.

**Por ejemplo:**

Si vamos a leer un texto titulado *La importancia de los bosques nativos*, podemos preguntarnos:

- ¿Qué sabemos sobre los bosques nativos?
- ¿Qué animales y plantas viven en ellos?
- ¿Por qué son importantes para nuestro entorno?

Estas preguntas nos ayudan a preparar nuestra mente para recibir nueva información.

## 2. Durante la lectura: identificar y relacionar información

Mientras leemos, podemos utilizar diferentes estrategias para comprender las ideas que presenta el texto.

**Identificar información explícita**

Es reconocer información que aparece directamente en el texto. Para encontrarla, podemos buscar nombres, fechas, lugares, acontecimientos y otros datos.

**Inferir información**

Consiste en descubrir información que el autor no expresa directamente, utilizando pistas del texto y nuestros conocimientos.

Por ejemplo, si leemos:

> Camila salió de su casa con paraguas y botas. Mientras caminaba, observó que las calles estaban cubiertas de agua.

Podemos inferir que probablemente estaba lloviendo, aunque el texto no lo diga expresamente.

**Releer cuando sea necesario**

Si encontramos una palabra, oración o párrafo que nos resulta difícil de comprender, podemos volver a leerlo con atención. A veces, una segunda lectura nos permite descubrir información que pasó inadvertida.

**Formular preguntas**

Hacer preguntas durante la lectura nos ayuda a mantener la atención y profundizar en las ideas.

Algunas preguntas útiles son:

- ¿De quién o de qué habla el texto?
- ¿Qué ocurrió?
- ¿Por qué sucedió?
- ¿Qué información importante nos entrega?

## 3. Después de leer: organizar y sintetizar la información

Cuando terminamos una lectura, podemos realizar algunas acciones para comprobar qué comprendimos y recordar las ideas más importantes.

**Identificar las ideas principales**

La idea principal corresponde a la información más importante de un párrafo o texto. Las ideas secundarias entregan detalles, explicaciones o ejemplos que complementan esa información.

**Organizar la información**

Podemos utilizar esquemas, cuadros comparativos o mapas conceptuales para relacionar las ideas y observar cómo se conectan entre sí.

**Elaborar un resumen**

Resumir consiste en expresar las ideas principales de un texto utilizando nuestras propias palabras y manteniendo su significado.

Un buen resumen permite recordar lo esencial de una lectura de manera clara y ordenada.

## 4. Pongamos en práctica lo aprendido

Imaginemos que leemos el siguiente texto:

### Las abejas y su importancia para el planeta

Las abejas cumplen una función fundamental en los ecosistemas. Al visitar las flores para obtener néctar y polen, transportan pequeñas partículas de polen de una flor a otra. Este proceso se conoce como polinización y permite que muchas plantas produzcan frutos y semillas.

Además, la polinización contribuye a la producción de numerosos alimentos que consumimos diariamente, como manzanas, frutillas y almendras.

Sin embargo, las abejas enfrentan diversas amenazas, entre ellas la pérdida de sus hábitats, el uso de ciertos pesticidas y el cambio climático.

### ¿Cómo aplicamos las estrategias?

- **Conocimientos previos:** pensamos en lo que sabemos sobre las abejas y su relación con las flores.
- **Información explícita:** identificamos que las abejas transportan polen entre las flores.
- **Inferencias:** podemos concluir que la disminución de las abejas podría afectar la producción de algunos alimentos.
- **Idea principal:** las abejas son fundamentales para los ecosistemas y la producción de alimentos.
- **Resumen:** las abejas contribuyen a la polinización, un proceso necesario para la reproducción de muchas plantas y la producción de alimentos. Actualmente enfrentan amenazas que ponen en riesgo su población.

## Para recordar

Comprender un texto es un proceso que podemos fortalecer con la práctica. Activar nuestros conocimientos, identificar información, realizar inferencias, formular preguntas y organizar las ideas nos permite aprovechar mejor cada lectura.

**Cada vez que leemos con atención, tenemos una nueva oportunidad para aprender.**$v8$, 4, 1),
    (2, $v8$# Comprendemos y analizamos textos narrativos

## ¿Qué hace interesante una historia?

Cuando leemos un cuento, una leyenda o una fábula, nos encontramos con personajes que viven experiencias, enfrentan dificultades y toman decisiones. Cada uno de estos elementos contribuye al desarrollo de la historia.

Comprender una narración significa descubrir qué sucede, quiénes participan, dónde ocurren los acontecimientos y por qué los personajes actúan de determinada manera.

En este contenido aprenderemos a reconocer los elementos de una narración y analizar cómo se relacionan entre sí.

## 1. ¿Qué es un texto narrativo?

Un texto narrativo es un relato que presenta una serie de acontecimientos protagonizados por uno o varios personajes. Estos acontecimientos pueden ser reales o imaginarios y se desarrollan en un tiempo y lugar determinados.

Existen distintos tipos de narraciones, entre ellas:

- **Cuentos:** relatos breves que presentan personajes y acontecimientos organizados en una historia.
- **Fábulas:** narraciones que generalmente utilizan animales como personajes y transmiten una enseñanza o moraleja.
- **Leyendas:** relatos tradicionales que mezclan elementos reales y fantásticos, y que forman parte de la cultura de una comunidad.
- **Mitos:** narraciones tradicionales que explican el origen del mundo, fenómenos naturales o creencias de una cultura.
- **Novelas:** narraciones extensas que desarrollan historias, personajes y conflictos con mayor profundidad.

## 2. Elementos de una narración

Para comprender una historia, debemos prestar atención a sus principales elementos.

### Los personajes

Son quienes participan en los acontecimientos de la historia. Cada personaje puede tener características, intereses, motivaciones y formas particulares de actuar.

Podemos distinguir:

- **Personaje principal:** participa directamente en los acontecimientos centrales de la historia.
- **Personajes secundarios:** acompañan al personaje principal y contribuyen al desarrollo de los acontecimientos.

También es importante observar cómo reaccionan los personajes frente a las situaciones que enfrentan. Sus decisiones nos permiten comprender sus motivaciones y características.

### El ambiente

Corresponde al entorno en el que ocurren los acontecimientos. Podemos reconocer dos dimensiones:

- **Ambiente físico:** lugar donde sucede la historia, como un bosque, una ciudad, una escuela o una montaña.
- **Ambiente social:** características de la comunidad, las costumbres, las relaciones y las condiciones de vida de los personajes.

El ambiente influye en las acciones de los personajes y en las situaciones que deben enfrentar.

### El tiempo

Corresponde al momento o período en que ocurren los acontecimientos. Una historia puede desarrollarse durante unas horas, varios días, años o incluso siglos.

Reconocer el tiempo nos ayuda a comprender el contexto de la narración y las circunstancias que rodean a sus personajes.

### El conflicto

Es la situación o dificultad que impulsa el desarrollo de la historia. Los personajes deben enfrentarla y tomar decisiones para avanzar.

Por ejemplo, un personaje puede tener que resolver un problema, superar un desafío o alcanzar un objetivo.

## 3. ¿Cómo se organiza una narración?

Generalmente, las narraciones presentan tres momentos que permiten organizar los acontecimientos.

**Inicio**

Se presentan los personajes, el ambiente y las primeras situaciones de la historia. El lector conoce el contexto en el que se desarrollarán los acontecimientos.

**Desarrollo**

Aparece el conflicto y se presentan las acciones que realizan los personajes para enfrentarlo. En esta parte suelen ocurrir los acontecimientos más importantes.

**Desenlace**

Se resuelve el conflicto y se conoce el resultado de las acciones de los personajes. La historia llega a su conclusión.

## 4. Analizamos las acciones y motivaciones de los personajes

Una buena comprensión lectora nos permite ir más allá de reconocer lo que sucede. También podemos analizar por qué suceden los acontecimientos y qué motivaciones tienen los personajes.

Por ejemplo, si un personaje decide compartir su alimento con alguien que tiene hambre, podemos preguntarnos:

- ¿Qué situación motivó su decisión?
- ¿Qué característica de su personalidad demuestra esta acción?
- ¿Qué consecuencias puede tener su decisión en la historia?

Al responder estas preguntas, utilizamos información del relato para fundamentar nuestras interpretaciones.

## 5. El lenguaje figurado en las narraciones

En algunas historias encontramos expresiones que utilizan las palabras de manera creativa para transmitir ideas, emociones o imágenes.

Por ejemplo:

**"El viento susurraba entre los árboles".**

El viento no puede hablar ni susurrar como una persona. Esta expresión utiliza una personificación para transmitir la sensación de un viento suave que se mueve entre los árboles.

Otro ejemplo:

**"Tenía un corazón de piedra".**

Esta expresión no significa que el corazón esté hecho de piedra. Se utiliza para representar a una persona que parece no demostrar sentimientos o compasión.

Reconocer estas expresiones nos permite comprender mejor lo que el autor quiere transmitir.

## 6. Pongamos en práctica lo aprendido

### La decisión de Martina

Una mañana de otoño, Martina caminaba por un sendero cercano a su casa. Le gustaba recorrer aquel lugar porque podía observar los árboles, escuchar el canto de los pájaros y disfrutar de la tranquilidad del bosque.

Mientras avanzaba, encontró una pequeña caja de madera junto a un árbol. Al abrirla, descubrió que contenía varias fotografías antiguas y una carta.

La carta estaba dirigida a una persona llamada Elena y explicaba que aquellas fotografías pertenecían a una familia que había vivido en el sector muchos años atrás.

Martina sintió curiosidad por conocer la historia de aquella familia. En lugar de llevarse la caja a su casa, decidió entregarla en la biblioteca del pueblo, donde podrían investigar su origen.

Días después, la bibliotecaria descubrió que las fotografías correspondían a los primeros habitantes de la localidad. Gracias al hallazgo, pudieron organizar una exposición para compartir aquella historia con toda la comunidad.

### Analicemos la narración

- **Personaje principal:** Martina, quien encuentra la caja y decide entregarla en la biblioteca.
- **Personajes secundarios:** la bibliotecaria y las personas relacionadas con la historia de las fotografías.
- **Ambiente físico:** un sendero del bosque y la biblioteca del pueblo.
- **Tiempo:** una mañana de otoño y los días posteriores al hallazgo.
- **Conflicto o situación central:** el descubrimiento de una caja con fotografías y una carta cuyo origen se desconoce.
- **Desenlace:** las fotografías permiten conocer parte de la historia de los primeros habitantes de la localidad.

### Reflexionemos

La decisión de Martina demuestra curiosidad y responsabilidad. Al entregar la caja en la biblioteca, permitió que otras personas pudieran investigar y conocer la historia de su comunidad.

Esta situación nos muestra cómo las acciones de un personaje pueden influir en el desarrollo de una historia y beneficiar a otras personas.

## Para recordar

Cuando leemos una narración, podemos comprenderla mejor si identificamos sus personajes, el ambiente, el tiempo y el conflicto.

También es importante analizar las acciones y motivaciones de los personajes, reconocer cómo se relacionan los acontecimientos e interpretar las expresiones que utiliza el autor.

**Leer una historia es descubrir mucho más que los acontecimientos: es comprender las decisiones, emociones y experiencias que dan vida a sus personajes.**$v8$, 4, 1),
    (3, $v8$# La fotosíntesis: cómo las plantas producen su alimento

## ¿Cómo obtienen alimento las plantas?

Las plantas son organismos fundamentales para la vida en nuestro planeta. A diferencia de los animales, no necesitan alimentarse de otros seres vivos para obtener la energía química que utilizan para desarrollarse. Producen su propio alimento mediante un proceso llamado **fotosíntesis**.

La fotosíntesis es un proceso mediante el cual las plantas utilizan la energía de la luz solar para transformar agua y dióxido de carbono en azúcares. Durante este proceso también liberan oxígeno al ambiente.

Este fenómeno ocurre principalmente en las hojas, donde se encuentran estructuras celulares llamadas cloroplastos, que contienen clorofila, un pigmento responsable de captar la energía luminosa.

## 1. ¿Qué necesitan las plantas para realizar la fotosíntesis?

Para que la fotosíntesis ocurra, las plantas necesitan tres elementos fundamentales:

### Agua

El agua es absorbida desde el suelo a través de las raíces y transportada hacia las hojas mediante estructuras conductoras de la planta.

Este elemento aporta la materia necesaria para que ocurran las transformaciones químicas propias de la fotosíntesis.

### Dióxido de carbono

Es un gas presente en el aire que ingresa principalmente a través de pequeñas aberturas de las hojas llamadas estomas.

El dióxido de carbono aporta el carbono que la planta utiliza para producir azúcares.

### Energía lumínica

Proviene principalmente del Sol y es captada por la clorofila presente en los cloroplastos.

Esta energía permite que ocurran las transformaciones necesarias para producir azúcares a partir del agua y el dióxido de carbono.

## 2. ¿Qué producen las plantas durante la fotosíntesis?

Como resultado de la fotosíntesis se producen dos elementos importantes:

**Azúcares:** son sustancias que almacenan energía química y que la planta utiliza para obtener energía, crecer y desarrollar sus estructuras.

**Oxígeno:** es liberado al ambiente y puede ser utilizado por numerosos organismos durante la respiración celular.

Por esta razón, la fotosíntesis es un proceso esencial para mantener la vida en los ecosistemas.

## 3. ¿Dónde ocurre la fotosíntesis?

La fotosíntesis ocurre principalmente en las hojas de las plantas.

Dentro de sus células encontramos los cloroplastos, que contienen clorofila. Este pigmento absorbe parte de la luz que llega desde el Sol y permite aprovechar su energía.

Podemos reconocer una relación entre las distintas estructuras de la planta:

- Las raíces absorben agua del suelo.
- Las hojas incorporan dióxido de carbono del aire.
- Los cloroplastos captan la energía lumínica.
- Las hojas liberan oxígeno al ambiente.

## 4. ¿Qué factores pueden influir en la fotosíntesis?

La disponibilidad de agua, dióxido de carbono y energía lumínica influye directamente en la fotosíntesis.

Por ejemplo, una planta que recibe suficiente agua y dióxido de carbono, pero permanece en un lugar con muy poca luz, tendrá dificultades para realizar este proceso a un ritmo adecuado.

Del mismo modo, la falta de agua puede limitar la fotosíntesis, incluso cuando la planta recibe abundante luz solar.

Por eso, estos tres elementos deben estar disponibles para que la planta pueda desarrollar adecuadamente este proceso.

## 5. La fotosíntesis y la historia de la ciencia

El conocimiento científico sobre la fotosíntesis se ha construido gracias al trabajo de diferentes investigadores.

A lo largo del tiempo, científicos como Jan Ingenhousz, Joseph Priestley y Melvin Calvin realizaron aportes importantes para comprender cómo las plantas interactúan con la luz, el aire y otras sustancias.

Sus investigaciones permitieron conocer mejor los procesos que ocurren en las plantas y explicar cómo producen azúcares y liberan oxígeno.

Este conocimiento también ha permitido comprender la importancia de la vegetación para los ecosistemas y para la vida en nuestro planeta.

## 6. Pongamos en práctica lo aprendido

Imaginemos que una estudiante realiza una investigación con dos plantas de características similares.

Ambas reciben la misma cantidad de agua y se encuentran en lugares con condiciones ambientales semejantes. Sin embargo, una de ellas permanece cerca de una ventana con abundante luz solar, mientras que la otra se mantiene durante varios días en un lugar oscuro.

Al observarlas, la estudiante registra diferencias en su desarrollo.

**¿Qué podemos analizar?**

- La luz solar proporciona la energía necesaria para la fotosíntesis.
- La disponibilidad de agua y dióxido de carbono también influye en este proceso.
- Una menor disponibilidad de luz puede disminuir la actividad fotosintética.
- Las observaciones permiten formular preguntas y desarrollar investigaciones experimentales.

## Para recordar

La fotosíntesis es un proceso mediante el cual las plantas utilizan agua, dióxido de carbono y energía lumínica para producir azúcares y liberar oxígeno.

Gracias a este proceso, los organismos fotosintéticos producen materia orgánica que constituye la base de muchas relaciones alimentarias en los ecosistemas.

**Las plantas transforman la energía de la luz solar en energía química almacenada en sustancias que permiten sostener la vida.**$v8$, 4, 3),
    (4, $v8$# Cadenas y redes alimentarias: cómo se relacionan los seres vivos

## ¿De dónde obtienen energía los seres vivos?

En los ecosistemas, los organismos necesitan obtener energía y materia para crecer, desarrollarse y realizar sus funciones vitales.

Las plantas, las algas y algunos microorganismos pueden producir su propio alimento mediante la fotosíntesis. Otros seres vivos obtienen energía alimentándose de plantas o de otros animales.

De esta manera, los organismos se relacionan mediante la alimentación, formando cadenas y redes alimentarias.

## 1. ¿Qué es una cadena alimentaria?

Una cadena alimentaria representa cómo se transfiere la materia y la energía entre los organismos de un ecosistema a través de la alimentación.

Cada organismo ocupa una posición dentro de esta cadena, dependiendo de cómo obtiene su alimento.

Por ejemplo:

**Pasto → Saltamontes → Rana → Serpiente → Águila**

En esta cadena:

- El pasto produce su propio alimento mediante la fotosíntesis.
- El saltamontes se alimenta del pasto.
- La rana se alimenta del saltamontes.
- La serpiente se alimenta de la rana.
- El águila se alimenta de la serpiente.

Las flechas indican la dirección en que se transfiere la materia y la energía: desde el organismo que sirve de alimento hacia el organismo que lo consume.

## 2. ¿Qué función cumplen los organismos en una cadena alimentaria?

Podemos clasificar a los organismos según la función que cumplen en la transferencia de materia y energía.

### Productores

Son organismos capaces de producir su propio alimento, principalmente mediante la fotosíntesis.

Por ejemplo:
- Plantas.
- Algas.
- Algunas bacterias.

Los productores constituyen la base de muchas cadenas alimentarias.

### Consumidores

Son organismos que obtienen materia y energía alimentándose de otros seres vivos.

Podemos distinguir:

- **Consumidores primarios:** se alimentan directamente de los productores. Por ejemplo, un conejo que come pasto.
- **Consumidores secundarios:** se alimentan de consumidores primarios. Por ejemplo, una serpiente que se alimenta de un ratón herbívoro.
- **Consumidores terciarios:** se alimentan de consumidores secundarios. Por ejemplo, un águila que captura una serpiente.

### Descomponedores

Son organismos que obtienen materia y energía al descomponer restos de seres vivos y desechos orgánicos.

Entre ellos encontramos hongos y bacterias.

Su función es fundamental porque permiten que los nutrientes regresen al ambiente y puedan ser aprovechados nuevamente por los organismos productores.

## 3. ¿Qué diferencia existe entre materia y energía?

En una cadena alimentaria se transfieren tanto materia como energía, pero ambas siguen dinámicas diferentes.

**La materia** circula entre los organismos y el ambiente. Por ejemplo, los nutrientes presentes en el suelo pueden ser absorbidos por las plantas y luego pasar a los animales que se alimentan de ellas. Mediante la descomposición, parte de esa materia regresa al ambiente.

**La energía** ingresa principalmente al ecosistema desde el Sol. Los organismos productores captan parte de esa energía mediante la fotosíntesis y la transfieren a otros organismos a través de la alimentación.

En cada transferencia, una parte de la energía se utiliza en las funciones vitales y se disipa al ambiente en forma de calor.

Por esta razón, la energía disponible disminuye a medida que avanzamos hacia los siguientes niveles de una cadena alimentaria.

## 4. ¿Qué es una red alimentaria?

En la naturaleza, los organismos suelen alimentarse de distintas especies y, al mismo tiempo, pueden servir de alimento para varios organismos.

Por esta razón, muchas cadenas alimentarias se conectan entre sí y forman una **red alimentaria**.

Una red alimentaria representa las múltiples relaciones de alimentación que existen dentro de un ecosistema.

Por ejemplo, en un ecosistema terrestre:

- El pasto puede ser consumido por conejos, saltamontes y ratones.
- Los saltamontes pueden ser consumidos por ranas y aves.
- Los ratones pueden ser consumidos por serpientes y águilas.
- Las serpientes pueden ser consumidas por águilas.

Estas relaciones muestran que los organismos dependen unos de otros para obtener materia y energía.

## 5. ¿Cómo afecta la actividad humana a las redes alimentarias?

Las actividades humanas pueden modificar las condiciones de los ecosistemas y afectar las relaciones alimentarias entre sus organismos.

Algunos ejemplos son:

**Deforestación:** la eliminación de grandes extensiones de vegetación reduce los hábitats y los recursos alimentarios de muchas especies.

**Contaminación:** la presencia de sustancias contaminantes en el agua, el suelo o el aire puede afectar a los productores, consumidores y descomponedores.

**Sobreexplotación de especies:** la captura o extracción excesiva de organismos puede disminuir sus poblaciones y alterar las relaciones alimentarias.

**Introducción de especies exóticas invasoras:** algunas especies introducidas pueden competir con las especies nativas o convertirse en nuevos depredadores, modificando el equilibrio del ecosistema.

Cuando una población disminuye considerablemente, otros organismos que dependen de ella pueden verse afectados. Por eso, las modificaciones en una parte de la red alimentaria pueden producir consecuencias en todo el ecosistema.

## 6. ¿Cómo podemos proteger las redes alimentarias?

El cuidado de los ecosistemas permite mantener las relaciones entre los seres vivos y conservar la biodiversidad.

Algunas acciones que contribuyen a su protección son:

- Evitar la contaminación de ríos, lagos y suelos.
- Proteger los bosques y la vegetación nativa.
- Respetar las áreas naturales protegidas.
- Evitar la extracción indiscriminada de especies.
- Participar en actividades de conservación y restauración ambiental.

Cada una de estas acciones contribuye a mantener las condiciones necesarias para que los organismos puedan desarrollarse y relacionarse.

## 7. Pongamos en práctica lo aprendido

Imaginemos un ecosistema donde encontramos pasto, conejos, zorros y hongos descomponedores.

Podemos representar algunas de sus relaciones de la siguiente manera:

**Pasto → Conejo → Zorro**

Además, cuando los organismos mueren, sus restos pueden ser descompuestos por los hongos, permitiendo que los nutrientes regresen al suelo.

Ahora imaginemos que una actividad humana provoca la desaparición de gran parte del pasto.

¿Qué podría ocurrir?

- Los conejos tendrían menos alimento disponible.
- La población de conejos podría disminuir.
- Los zorros podrían encontrar menos alimento.
- Las relaciones alimentarias del ecosistema se verían modificadas.

Este ejemplo demuestra que los organismos están conectados y que los cambios en una población pueden afectar a otras especies.

## Para recordar

Las cadenas y redes alimentarias representan las relaciones mediante las cuales los seres vivos transfieren materia y energía.

Los productores, consumidores y descomponedores cumplen funciones fundamentales para el funcionamiento de los ecosistemas.

Las actividades humanas pueden modificar estas relaciones, por lo que proteger los ambientes naturales es esencial para conservar la biodiversidad.

**En un ecosistema, cada organismo cumple una función y forma parte de una red de relaciones que sostiene la vida.**$v8$, 4, 3),
    (5, $v8$# Factores, múltiplos y números primos

## ¿Cómo se relacionan los números entre sí?

En matemática, los números tienen propiedades que nos permiten establecer relaciones entre ellos. Por ejemplo, podemos descubrir cuándo un número puede dividirse exactamente por otro, cuáles son sus múltiplos y cómo se pueden descomponer en factores.

Estas relaciones son muy útiles para resolver problemas cotidianos, como organizar objetos en grupos iguales, distribuir materiales o calcular cuándo coinciden determinados acontecimientos.

En este contenido aprenderemos a reconocer múltiplos, identificar factores y distinguir entre números primos y compuestos.

## 1. ¿Qué son los múltiplos?

Los múltiplos de un número se obtienen al multiplicarlo por los números naturales.

Por ejemplo, para encontrar los múltiplos de 4, multiplicamos:

- 4 × 1 = 4
- 4 × 2 = 8
- 4 × 3 = 12
- 4 × 4 = 16
- 4 × 5 = 20

Por lo tanto, algunos múltiplos de 4 son:

**4, 8, 12, 16, 20, 24, 28, 32...**

Los múltiplos continúan indefinidamente, por lo que cada número natural tiene infinitos múltiplos.

### ¿Cómo podemos reconocer un múltiplo?

Un número es múltiplo de otro cuando se obtiene mediante una multiplicación por un número natural.

Por ejemplo:

- 30 es múltiplo de 5 porque 5 × 6 = 30.
- 42 es múltiplo de 7 porque 7 × 6 = 42.
- 25 es múltiplo de 4 porque no existe un número natural que multiplicado por 4 dé exactamente 25: en realidad, 25 no es múltiplo de 4.

## 2. ¿Qué son los factores o divisores?

Los factores de un número son aquellos números naturales que permiten dividirlo exactamente, es decir, sin dejar resto.

Por ejemplo, analicemos el número 12.

Podemos dividirlo exactamente por:

- 1, porque 12 ÷ 1 = 12.
- 2, porque 12 ÷ 2 = 6.
- 3, porque 12 ÷ 3 = 4.
- 4, porque 12 ÷ 4 = 3.
- 6, porque 12 ÷ 6 = 2.
- 12, porque 12 ÷ 12 = 1.

Por lo tanto, los factores de 12 son:

**1, 2, 3, 4, 6 y 12.**

### Relación entre factores y múltiplos

Los factores y los múltiplos están directamente relacionados.

Si 3 es factor de 18, significa que 18 es múltiplo de 3.

Podemos comprobarlo mediante la multiplicación:

3 × 6 = 18

Esta relación nos permite reconocer fácilmente cuándo un número es divisor o múltiplo de otro.

## 3. Números primos y compuestos

Los números naturales mayores que 1 pueden clasificarse, según su cantidad de divisores, en números primos y compuestos.

### Números primos

Un número primo es aquel que tiene exactamente dos divisores: el número 1 y él mismo.

Por ejemplo:

- 2 tiene como divisores al 1 y al 2.
- 3 tiene como divisores al 1 y al 3.
- 5 tiene como divisores al 1 y al 5.
- 7 tiene como divisores al 1 y al 7.

Por esta razón, 2, 3, 5 y 7 son números primos.

El número 2 tiene una característica especial: es el único número primo par.

### Números compuestos

Un número compuesto tiene más de dos divisores.

Por ejemplo, el número 12 tiene seis divisores: 1, 2, 3, 4, 6 y 12.

Otros ejemplos de números compuestos son 4, 6, 8, 9, 10 y 15.

**Importante:** el número 1 no es primo ni compuesto, porque tiene un solo divisor.

## 4. ¿Cómo podemos descomponer un número en factores primos?

Todo número compuesto puede expresarse como una multiplicación de números primos.

Este procedimiento se conoce como descomposición en factores primos.

Por ejemplo, descompongamos el número 24:

Primero, podemos dividirlo por 2:

24 ÷ 2 = 12

Luego continuamos dividiendo:

12 ÷ 2 = 6

6 ÷ 2 = 3

Finalmente, 3 es un número primo.

Por lo tanto:

24 = 2 × 2 × 2 × 3

Esta representación permite identificar los factores primos que componen un número.

## 5. ¿Para qué sirven los múltiplos y factores en la vida cotidiana?

Estas propiedades matemáticas nos permiten resolver situaciones relacionadas con la organización y distribución.

**Ejemplo 1: Organización de materiales**

Una profesora tiene 24 lápices y quiere distribuirlos en grupos con la misma cantidad de lápices, sin que sobre ninguno.

Puede formar grupos de:

- 2 grupos de 12 lápices.
- 3 grupos de 8 lápices.
- 4 grupos de 6 lápices.
- 6 grupos de 4 lápices.
- 8 grupos de 3 lápices.
- 12 grupos de 2 lápices.

En este caso, utilizamos los factores de 24 para encontrar distintas formas de distribuir los lápices.

**Ejemplo 2: Coincidencia de acontecimientos**

Una luz se enciende cada 4 segundos y otra cada 6 segundos. Si ambas se encienden al mismo tiempo, podemos utilizar los múltiplos de ambos números para descubrir cuándo volverán a coincidir.

Múltiplos de 4: 4, 8, 12, 16, 20, 24...

Múltiplos de 6: 6, 12, 18, 24, 30...

El primer múltiplo común es 12.

Por lo tanto, ambas luces volverán a encenderse simultáneamente después de 12 segundos.

## Para recordar

- Los múltiplos se obtienen mediante la multiplicación de un número por números naturales.
- Los factores o divisores permiten dividir un número exactamente.
- Los números primos tienen exactamente dos divisores.
- Los números compuestos tienen más de dos divisores.
- El número 1 no es primo ni compuesto.

**Comprender estas relaciones nos permite reconocer propiedades de los números y utilizarlas para resolver diferentes situaciones matemáticas.**$v8$, 4, 2),
    (6, $v8$# Antecedentes de la Independencia de América y Chile

## ¿Por qué las colonias americanas comenzaron a buscar su independencia?

Durante varios siglos, gran parte del continente americano estuvo bajo el dominio de las monarquías europeas. En el caso de Chile y de muchos otros territorios americanos, el poder político dependía de la Corona española. Las autoridades eran designadas por el rey y las decisiones más importantes se tomaban siguiendo sus intereses.

Sin embargo, hacia fines del siglo XVIII y comienzos del siglo XIX, comenzaron a producirse cambios políticos, económicos y sociales que debilitaron el dominio español. Distintos acontecimientos ocurridos en Europa y América influyeron en las ideas de quienes vivían en las colonias.

La independencia no fue el resultado de una sola causa ni ocurrió al mismo tiempo en todos los territorios. Fue un proceso continental en el que participaron distintos grupos y que tuvo características particulares en cada lugar.

## 1. Antecedentes externos

Los antecedentes externos fueron acontecimientos e ideas que surgieron fuera de las colonias españolas y que influyeron en los movimientos independentistas americanos.

### La Ilustración

Durante el siglo XVIII se difundió en Europa un movimiento intelectual conocido como la Ilustración. Sus pensadores defendían el uso de la razón, la libertad y la igualdad ante la ley. También cuestionaban que una sola persona, como un rey, concentrara todo el poder político.

Estas ideas promovieron nuevas formas de entender el gobierno y la organización de la sociedad. Algunos criollos americanos conocieron estos planteamientos mediante libros, publicaciones y viajes.

### La independencia de Estados Unidos (1776)

Las trece colonias inglesas de América del Norte declararon su independencia de Gran Bretaña en 1776, después de un conflicto con la monarquía británica.

Este acontecimiento mostró que un territorio americano podía separarse de una potencia europea y organizar un gobierno propio. También difundió ideas relacionadas con la libertad y la representación política.

### La Revolución francesa (1789)

En 1789 comenzó la Revolución francesa, un proceso que cuestionó el poder absoluto del rey y los privilegios de ciertos grupos sociales.

Entre sus principales ideas estuvieron la libertad, la igualdad ante la ley y la soberanía nacional. Esta última plantea que el poder político tiene su origen en la nación y no únicamente en la voluntad de un monarca.

La Revolución francesa tuvo una gran influencia en la difusión de ideas políticas que también llegaron a América.

### La independencia de Haití (1804)

Haití fue una colonia francesa del Caribe donde se produjo una revolución protagonizada principalmente por personas esclavizadas y libres de ascendencia africana.

Después de años de conflicto, Haití declaró su independencia en 1804. Fue un acontecimiento importante porque se convirtió en el primer Estado independiente de América Latina y el Caribe y demostró que el sistema colonial podía ser cuestionado por distintos sectores de la población.

### La invasión napoleónica a España (1808)

En 1808, las tropas de Napoleón Bonaparte invadieron España. En medio de la crisis política, el rey Fernando VII fue apartado del trono y quedó prisionero. Napoleón puso a su hermano, José Bonaparte, como nuevo rey de España.

La situación generó una pregunta fundamental: ¿quién debía gobernar los territorios españoles si el rey legítimo estaba prisionero?

En España se organizaron juntas de gobierno para resistir la ocupación francesa. La crisis también tuvo consecuencias en América, donde comenzaron a surgir propuestas para formar gobiernos locales.

## 2. Antecedentes internos

Los antecedentes internos fueron situaciones que se desarrollaron dentro de las colonias americanas y que aumentaron el descontento con el dominio español.

### El descontento de los criollos

En la sociedad colonial existían diferencias entre los grupos sociales. Los españoles nacidos en España, llamados peninsulares, solían ocupar los cargos políticos y administrativos más importantes.

Los criollos, descendientes de españoles nacidos en América, podían tener importantes riquezas y propiedades, pero encontraban limitaciones para acceder a los cargos de mayor autoridad.

Esta situación generó descontento entre algunos sectores criollos, que comenzaron a cuestionar la desigualdad en el acceso al poder.

### Las reformas de la Corona española

Durante el siglo XVIII, los reyes de la dinastía Borbón impulsaron reformas para fortalecer el control sobre sus colonias y aumentar los ingresos de la Corona.

Estas medidas modificaron la administración y el comercio colonial. Aunque buscaban mejorar los recursos y el control de España, también provocaron conflictos de intereses entre comerciantes y grupos locales.

### El desarrollo de una identidad americana

Con el paso del tiempo, algunos criollos comenzaron a desarrollar un mayor sentido de pertenencia hacia los territorios donde habían nacido y vivido.

Aunque compartían elementos culturales con España, sus intereses y experiencias estaban relacionados con América. Esto contribuyó a que algunos cuestionaran la autoridad de la Corona y defendieran una mayor participación en las decisiones políticas.

## 3. La Independencia de Chile como parte de un proceso continental

Los acontecimientos ocurridos en Chile no fueron aislados. A comienzos del siglo XIX, distintas colonias españolas de América comenzaron a organizar gobiernos propios y posteriormente a luchar por su independencia.

La crisis de la monarquía española en 1808 fue un factor decisivo para el desarrollo de estos movimientos. En territorios como Nueva Granada, Venezuela, el Río de la Plata, México y Chile surgieron iniciativas políticas que cuestionaron la autoridad de las instituciones españolas.

Sin embargo, cada territorio vivió un proceso diferente. Algunos comenzaron antes, otros enfrentaron conflictos más prolongados y las condiciones sociales y políticas no fueron iguales en todo el continente.

En Chile, la crisis de la monarquía española favoreció la organización de un gobierno local en 1810. Este hecho marcó el inicio de un proceso que, después de varios años de conflictos y cambios políticos, conduciría a la independencia del país.

## 4. ¿Qué debemos recordar?

La independencia de Chile y de otras colonias americanas fue consecuencia de múltiples antecedentes:

- **Ideas ilustradas:** difundieron principios como la libertad, la igualdad ante la ley y la soberanía.
- **Independencia de Estados Unidos:** mostró que una colonia americana podía separarse de una potencia europea.
- **Revolución francesa:** cuestionó la monarquía absoluta y difundió nuevas ideas políticas.
- **Independencia de Haití:** mostró la fuerza de los movimientos contra el sistema colonial y la esclavitud.
- **Invasión napoleónica a España:** provocó una crisis de la monarquía que abrió un debate sobre quién debía gobernar.
- **Descontento criollo:** surgió por las limitaciones políticas y los conflictos económicos existentes en las colonias.

Comprender estos antecedentes permite reconocer que la Independencia de Chile formó parte de un proceso mucho más amplio: la transformación política de América durante las primeras décadas del siglo XIX.$v8$, 4, 4);

-- 3. Vinculos OA-contenido con los ids de contenido del mobile.
INSERT INTO objetivo_aprendizaje_contenido (oa_contenido_id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 1),
    (3, 2, 1, 1),
    (4, 2, 2, 1),
    (5, 5, 3, 2),
    (6, 5, 4, 2),
    (7, 3, 5, 3),
    (8, 3, 6, 3),
    (9, 4, 5, 3),
    (10, 4, 6, 3),
    (11, 6, 7, 4),
    (12, 6, 8, 4);

-- 4. Recursos de contenido: el esquema PostgreSQL exige recurso_id en
--    pregunta y alternativa, a diferencia del mobile (que usa NULL).
INSERT INTO recurso (recurso_id, nombre, url, descripcion, tipo_recurso_id, contenido_id) VALUES
    (1, $v8$Guia de estrategias de comprension lectora$v8$, NULL, 'Material de apoyo del contenido.', 3, 1),
    (2, $v8$Guia de textos narrativos$v8$, NULL, 'Material de apoyo del contenido.', 3, 2),
    (3, $v8$Guia de fotosintesis$v8$, NULL, 'Material de apoyo del contenido.', 3, 3),
    (4, $v8$Guia de cadenas y redes alimentarias$v8$, NULL, 'Material de apoyo del contenido.', 3, 4),
    (5, $v8$Guia de numeros y operaciones$v8$, NULL, 'Material de apoyo del contenido.', 3, 5),
    (6, $v8$Guia de independencia$v8$, NULL, 'Material de apoyo del contenido.', 3, 6);

-- 5. Actividades: 8 formativas y 4 pruebas finales, todas PUBLICADO.
INSERT INTO actividad (actividad_id, instrucciones, tipo_actividad_id, contenido_id, estado_id) VALUES
    (1, $v8$Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 1, 4),
    (2, $v8$Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 2, 4),
    (3, $v8$Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 3, 4),
    (4, $v8$Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 4, 4),
    (5, $v8$Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 5, 4),
    (6, $v8$Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 5, 4),
    (7, $v8$Lee cuidadosamente cada pregunta y selecciona la alternativa correcta. Cada pregunta tiene cuatro alternativas y solo una respuesta válida. Cada respuesta correcta equivale a un punto.$v8$, 3, 5, 4),
    (8, $v8$Lee atentamente cada pregunta sobre los antecedentes externos de la Independencia y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 6, 4),
    (9, $v8$Lee atentamente cada pregunta sobre los antecedentes internos y el proceso continental y selecciona la alternativa correcta. Solo una alternativa es válida.$v8$, 1, 6, 4),
    (10, $v8$Evaluación sumativa final de la Unidad de Historia de Chile. Lee con atención cada enunciado y selecciona la opción correcta.$v8$, 3, 6, 4),
    (11, $v8$Evaluación sumativa final de la Unidad de Lectura - Comprensión. Lee con atención cada enunciado y selecciona la opción correcta.$v8$, 3, 1, 4),
    (12, $v8$Evaluación sumativa final de la Unidad de Ciencias de la Vida. Lee con atención cada enunciado y selecciona la opción correcta.$v8$, 3, 3, 4);

-- 6. Publicacion en ambos cursos (6A y 6B).
INSERT INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, fecha_inicio, fecha_fin, habilitada, visible, max_intentos) VALUES
    (1, 1, 1, NULL, NULL, TRUE, TRUE, 3),
    (2, 1, 2, NULL, NULL, TRUE, TRUE, 3),
    (3, 2, 1, NULL, NULL, TRUE, TRUE, 3),
    (4, 2, 2, NULL, NULL, TRUE, TRUE, 3),
    (5, 3, 1, NULL, NULL, TRUE, TRUE, 3),
    (6, 3, 2, NULL, NULL, TRUE, TRUE, 3),
    (7, 4, 1, NULL, NULL, TRUE, TRUE, 3),
    (8, 4, 2, NULL, NULL, TRUE, TRUE, 3),
    (9, 5, 1, NULL, NULL, TRUE, TRUE, 3),
    (10, 5, 2, NULL, NULL, TRUE, TRUE, 3),
    (11, 6, 1, NULL, NULL, TRUE, TRUE, 3),
    (12, 6, 2, NULL, NULL, TRUE, TRUE, 3),
    (13, 7, 1, NULL, NULL, TRUE, TRUE, 3),
    (14, 7, 2, NULL, NULL, TRUE, TRUE, 3),
    (15, 8, 1, NULL, NULL, TRUE, TRUE, 3),
    (16, 8, 2, NULL, NULL, TRUE, TRUE, 3),
    (17, 9, 1, NULL, NULL, TRUE, TRUE, 3),
    (18, 9, 2, NULL, NULL, TRUE, TRUE, 3),
    (19, 10, 1, NULL, NULL, TRUE, TRUE, 3),
    (20, 10, 2, NULL, NULL, TRUE, TRUE, 3),
    (21, 11, 1, NULL, NULL, TRUE, TRUE, 3),
    (22, 11, 2, NULL, NULL, TRUE, TRUE, 3),
    (23, 12, 1, NULL, NULL, TRUE, TRUE, 3),
    (24, 12, 2, NULL, NULL, TRUE, TRUE, 3);

-- 7. Preguntas. recurso_id apunta al contenido de la actividad que las usa.
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (1, $v8$¿Cuál es el principal propósito de activar nuestros conocimientos previos antes de leer un texto?$v8$, 1);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (2, $v8$Lee el siguiente fragmento:

"Martín observó por la ventana. El cielo estaba oscuro, los árboles se movían con fuerza y las primeras gotas golpeaban el techo. Rápidamente, fue a buscar su chaqueta impermeable".

¿Qué podemos inferir a partir de la información del texto?$v8$, 1);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (3, $v8$¿Cuál de las siguientes opciones describe mejor lo que significa elaborar un resumen?$v8$, 1);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (4, $v8$¿Cuál es la función principal del conflicto dentro de una narración?$v8$, 2);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (5, $v8$Lee el siguiente fragmento:

"Pedro encontró una billetera en el patio de su escuela. Aunque necesitaba dinero para comprar materiales, decidió entregarla a su profesora para que pudiera encontrar a su dueño".

¿Qué podemos concluir sobre la actitud de Pedro?$v8$, 2);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (6, $v8$¿Qué significa la expresión 'El sol sonreía sobre los campos' dentro de una narración?$v8$, 2);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (7, $v8$¿Cuáles son los tres elementos fundamentales que necesitan las plantas para realizar la fotosíntesis?$v8$, 3);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (8, $v8$¿Qué función cumple la clorofila durante la fotosíntesis?$v8$, 3);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (9, $v8$Una planta recibe suficiente agua y dióxido de carbono, pero permanece varios días en un lugar con muy poca luz. ¿Qué consecuencia es más probable?$v8$, 3);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (10, $v8$En la siguiente cadena alimentaria:

Pasto → Saltamontes → Rana → Serpiente

¿Qué función cumple el saltamontes?$v8$, 4);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (11, $v8$¿Por qué se considera que las redes alimentarias representan mejor las relaciones de alimentación de un ecosistema que las cadenas alimentarias?$v8$, 4);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (12, $v8$En un bosque se produce una deforestación que reduce considerablemente la cantidad de plantas disponibles. ¿Cuál podría ser una consecuencia de esta situación?$v8$, 4);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (13, $v8$¿Cuál de los siguientes números es múltiplo de 7?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (14, $v8$¿Cuál de las siguientes alternativas contiene todos los divisores del número 18?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (15, $v8$Una profesora tiene 36 cuadernos y quiere distribuirlos en grupos con la misma cantidad de cuadernos, sin que sobre ninguno. ¿Cuál de las siguientes cantidades de grupos es posible?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (16, $v8$¿Cuál de los siguientes números es primo?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (17, $v8$¿Por qué el número 15 se clasifica como compuesto?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (18, $v8$¿Cuál es la descomposición correcta del número 30 en factores primos?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (19, $v8$¿Cuál de los siguientes números es múltiplo de 9?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (20, $v8$¿Cuál de las siguientes alternativas presenta los primeros cinco múltiplos positivos de 6?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (21, $v8$¿Cuál de los siguientes números es divisor de 48?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (22, $v8$¿Cuál es el conjunto completo de divisores del número 20?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (23, $v8$¿Cuál de los siguientes números es primo?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (24, $v8$¿Por qué el número 1 no se considera primo ni compuesto?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (25, $v8$¿Cuál es la descomposición correcta del número 36 en factores primos?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (26, $v8$¿Cuál es la descomposición en factores primos del número 50?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (27, $v8$En una escuela hay 24 estudiantes que deben organizarse en equipos con igual cantidad de integrantes, sin que sobre ninguno. ¿Cuál de las siguientes cantidades de integrantes por equipo permite realizar esta distribución?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (28, $v8$Dos autobuses salen de un terminal al mismo tiempo. Uno sale cada 8 minutos y el otro cada 12 minutos. ¿Después de cuántos minutos volverán a salir simultáneamente?$v8$, 5);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (29, $v8$¿Qué idea promovida por la Ilustración cuestionaba el poder absoluto de los monarcas?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (30, $v8$¿Por qué fue importante la independencia de Estados Unidos en 1776 para las colonias americanas?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (31, $v8$¿Qué consecuencia política importante tuvo la Revolución francesa de 1789?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (32, $v8$¿Por qué la independencia de Haití, declarada en 1804, fue un acontecimiento significativo?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (33, $v8$¿Qué ocurrió en España en 1808 y cómo influyó en las colonias americanas?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (34, $v8$¿Quiénes eran los criollos durante el período colonial?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (35, $v8$¿Por qué algunos criollos estaban descontentos con el sistema colonial español?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (36, $v8$¿Cuál fue uno de los principales objetivos de las reformas borbónicas del siglo XVIII?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (37, $v8$¿Qué relación existió entre la crisis de la monarquía española y los movimientos de independencia en América?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (38, $v8$¿Por qué se afirma que la Independencia de Chile formó parte de un proceso continental?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (39, $v8$¿Cuál de las siguientes alternativas reúne antecedentes que influyeron en los procesos de independencia de las colonias americanas?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (40, $v8$¿Qué importancia tuvo la Primera Junta Nacional de Gobierno, formada el 18 de septiembre de 1810?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (41, $v8$¿Cuál fue una característica importante de la Constitución chilena de 1833?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (42, $v8$¿Qué aporte realizó Andrés Bello al desarrollo cultural y educativo de Chile durante el siglo XIX?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (43, $v8$¿Qué consecuencia territorial tuvo la Guerra del Pacífico para Chile?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (44, $v8$¿Qué consecuencia social estuvo relacionada con el auge de la explotación del salitre?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (45, $v8$¿Cuál de los siguientes acontecimientos representa un avance en la democratización de la sociedad chilena durante el siglo XX?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (46, $v8$¿Qué ocurrió el 11 de septiembre de 1973 en Chile?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (47, $v8$¿Qué importancia tuvo el plebiscito de 1988 en el proceso de recuperación de la democracia?$v8$, 6);
INSERT INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (48, $v8$¿Cuál de los siguientes ejemplos representa una continuidad y un cambio en la sociedad chilena a lo largo de su historia?$v8$, 6);

-- 8. Alternativas (4 por pregunta; una correcta).
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (1, $v8$Memorizar todas las palabras que aparecen en la lectura.$v8$, FALSE, 1),
    (2, $v8$Anticipar el tema y relacionar la información con lo que ya sabemos.$v8$, TRUE, 1),
    (3, $v8$Identificar inmediatamente todas las ideas secundarias.$v8$, FALSE, 1),
    (4, $v8$Elaborar un resumen antes de comenzar la lectura.$v8$, FALSE, 1);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (5, $v8$Martín tenía que ir a comprar una chaqueta nueva.$v8$, FALSE, 1),
    (6, $v8$Martín estaba esperando a un amigo.$v8$, FALSE, 1),
    (7, $v8$Se aproximaba una tormenta o estaba comenzando a llover intensamente.$v8$, TRUE, 1),
    (8, $v8$Martín quería salir a jugar con sus amigos.$v8$, FALSE, 1);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (9, $v8$Copiar todas las oraciones del texto en el mismo orden.$v8$, FALSE, 1),
    (10, $v8$Expresar las ideas principales utilizando nuestras propias palabras.$v8$, TRUE, 1),
    (11, $v8$Escribir solamente nuestra opinión sobre el tema.$v8$, FALSE, 1),
    (12, $v8$Seleccionar las palabras más difíciles y buscar su significado.$v8$, FALSE, 1);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (13, $v8$Describir físicamente a todos los personajes.$v8$, FALSE, 2),
    (14, $v8$Presentar la época en que fue escrito el relato.$v8$, FALSE, 2),
    (15, $v8$Introducir una situación o dificultad que impulsa el desarrollo de la historia.$v8$, TRUE, 2),
    (16, $v8$Explicar la moraleja al comienzo del relato.$v8$, FALSE, 2);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (17, $v8$Es una persona distraída que suele perder sus pertenencias.$v8$, FALSE, 2),
    (18, $v8$Demuestra honestidad al decidir devolver algo que no le pertenece.$v8$, TRUE, 2),
    (19, $v8$Está preocupado porque no tiene materiales escolares.$v8$, FALSE, 2),
    (20, $v8$Quiere recibir una recompensa por parte de su profesora.$v8$, FALSE, 2);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (21, $v8$Que el sol tiene un rostro y puede expresar emociones.$v8$, FALSE, 2),
    (22, $v8$Que los campos estaban iluminados por la luz del sol, utilizando una personificación.$v8$, TRUE, 2),
    (23, $v8$Que una persona estaba sonriendo mientras observaba el sol.$v8$, FALSE, 2),
    (24, $v8$Que el sol estaba desapareciendo detrás de las montañas.$v8$, FALSE, 2);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (25, $v8$Oxígeno, azúcar y energía térmica.$v8$, FALSE, 3),
    (26, $v8$Agua, dióxido de carbono y energía lumínica.$v8$, TRUE, 3),
    (27, $v8$Agua, oxígeno y sales minerales.$v8$, FALSE, 3),
    (28, $v8$Dióxido de carbono, oxígeno y energía térmica.$v8$, FALSE, 3);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (29, $v8$Absorber la energía luminosa para que pueda utilizarse en la fotosíntesis.$v8$, TRUE, 3),
    (30, $v8$Transportar agua desde las raíces hasta las hojas.$v8$, FALSE, 3),
    (31, $v8$Liberar dióxido de carbono hacia el ambiente.$v8$, FALSE, 3),
    (32, $v8$Transformar directamente el oxígeno en azúcar.$v8$, FALSE, 3);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (33, $v8$La planta producirá más oxígeno que una planta iluminada.$v8$, FALSE, 3),
    (34, $v8$La planta dejará de necesitar agua para desarrollarse.$v8$, FALSE, 3),
    (35, $v8$La actividad fotosintética disminuirá debido a la menor disponibilidad de energía lumínica.$v8$, TRUE, 3),
    (36, $v8$La planta comenzará a producir dióxido de carbono mediante la fotosíntesis.$v8$, FALSE, 3);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (37, $v8$Productor.$v8$, FALSE, 4),
    (38, $v8$Consumidor primario.$v8$, TRUE, 4),
    (39, $v8$Consumidor secundario.$v8$, FALSE, 4),
    (40, $v8$Descomponedor.$v8$, FALSE, 4);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (41, $v8$Porque muestran que todos los organismos se alimentan exclusivamente de plantas.$v8$, FALSE, 4),
    (42, $v8$Porque representan una única secuencia de alimentación entre organismos.$v8$, FALSE, 4),
    (43, $v8$Porque muestran las múltiples conexiones entre diferentes cadenas alimentarias.$v8$, TRUE, 4),
    (44, $v8$Porque explican solamente cómo los descomponedores obtienen energía.$v8$, FALSE, 4);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (45, $v8$Los consumidores primarios tendrían más alimento disponible.$v8$, FALSE, 4),
    (46, $v8$Las redes alimentarias permanecerían sin cambios.$v8$, FALSE, 4),
    (47, $v8$Los productores aumentarían automáticamente su población.$v8$, FALSE, 4),
    (48, $v8$Los herbívoros podrían disminuir por la falta de alimento, afectando también a sus depredadores.$v8$, TRUE, 4);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (49, $v8$32$v8$, FALSE, 5),
    (50, $v8$42$v8$, TRUE, 5),
    (51, $v8$45$v8$, FALSE, 5),
    (52, $v8$50$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (53, $v8$1, 2, 3, 6, 9 y 18.$v8$, TRUE, 5),
    (54, $v8$1, 2, 4, 6, 9 y 18.$v8$, FALSE, 5),
    (55, $v8$1, 3, 5, 6, 9 y 18.$v8$, FALSE, 5),
    (56, $v8$2, 3, 6, 9 y 18.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (57, $v8$5 grupos.$v8$, FALSE, 5),
    (58, $v8$7 grupos.$v8$, FALSE, 5),
    (59, $v8$8 grupos.$v8$, FALSE, 5),
    (60, $v8$9 grupos.$v8$, TRUE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (61, $v8$21$v8$, FALSE, 5),
    (62, $v8$27$v8$, FALSE, 5),
    (63, $v8$29$v8$, TRUE, 5),
    (64, $v8$33$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (65, $v8$Porque es divisible solamente por 1 y por 15.$v8$, FALSE, 5),
    (66, $v8$Porque tiene más de dos divisores: 1, 3, 5 y 15.$v8$, TRUE, 5),
    (67, $v8$Porque es un número impar.$v8$, FALSE, 5),
    (68, $v8$Porque no puede dividirse exactamente por ningún número.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (69, $v8$2 × 3 × 5.$v8$, TRUE, 5),
    (70, $v8$2 × 2 × 5.$v8$, FALSE, 5),
    (71, $v8$3 × 5 × 5.$v8$, FALSE, 5),
    (72, $v8$2 × 3 × 3.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (73, $v8$45$v8$, TRUE, 5),
    (74, $v8$52$v8$, FALSE, 5),
    (75, $v8$64$v8$, FALSE, 5),
    (76, $v8$70$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (77, $v8$6, 12, 18, 24, 30.$v8$, TRUE, 5),
    (78, $v8$6, 10, 14, 18, 22.$v8$, FALSE, 5),
    (79, $v8$1, 6, 12, 18, 24.$v8$, FALSE, 5),
    (80, $v8$6, 12, 16, 24, 36.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (81, $v8$5$v8$, FALSE, 5),
    (82, $v8$7$v8$, FALSE, 5),
    (83, $v8$8$v8$, TRUE, 5),
    (84, $v8$10$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (85, $v8$1, 2, 4, 5, 10 y 20.$v8$, TRUE, 5),
    (86, $v8$1, 2, 3, 4, 5 y 20.$v8$, FALSE, 5),
    (87, $v8$2, 4, 5, 10 y 20.$v8$, FALSE, 5),
    (88, $v8$1, 2, 4, 6, 10 y 20.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (89, $v8$35$v8$, FALSE, 5),
    (90, $v8$39$v8$, FALSE, 5),
    (91, $v8$41$v8$, TRUE, 5),
    (92, $v8$49$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (93, $v8$Porque es un número impar.$v8$, FALSE, 5),
    (94, $v8$Porque solamente tiene un divisor positivo.$v8$, TRUE, 5),
    (95, $v8$Porque no puede dividirse por sí mismo.$v8$, FALSE, 5),
    (96, $v8$Porque es múltiplo de todos los números.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (97, $v8$2 × 2 × 3 × 3.$v8$, TRUE, 5),
    (98, $v8$2 × 3 × 6.$v8$, FALSE, 5),
    (99, $v8$3 × 3 × 4.$v8$, FALSE, 5),
    (100, $v8$2 × 2 × 2 × 3.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (101, $v8$2 × 5 × 5.$v8$, TRUE, 5),
    (102, $v8$2 × 2 × 5.$v8$, FALSE, 5),
    (103, $v8$5 × 5 × 5.$v8$, FALSE, 5),
    (104, $v8$2 × 25 × 5.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (105, $v8$5 estudiantes.$v8$, FALSE, 5),
    (106, $v8$7 estudiantes.$v8$, FALSE, 5),
    (107, $v8$8 estudiantes.$v8$, TRUE, 5),
    (108, $v8$10 estudiantes.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (109, $v8$16 minutos.$v8$, FALSE, 5),
    (110, $v8$20 minutos.$v8$, FALSE, 5),
    (111, $v8$24 minutos.$v8$, TRUE, 5),
    (112, $v8$32 minutos.$v8$, FALSE, 5);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (113, $v8$Que el poder político debía concentrarse en una familia real.$v8$, FALSE, 6),
    (114, $v8$Que la autoridad debía tener su origen en la nación.$v8$, TRUE, 6),
    (115, $v8$Que los privilegios sociales debían mantenerse por nacimiento.$v8$, FALSE, 6),
    (116, $v8$Que las colonias debían depender políticamente de Europa.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (117, $v8$Porque permitió que España recuperara sus colonias.$v8$, FALSE, 6),
    (118, $v8$Porque demostró que una colonia podía independizarse de una potencia europea.$v8$, TRUE, 6),
    (119, $v8$Porque provocó la desaparición de todas las monarquías europeas.$v8$, FALSE, 6),
    (120, $v8$Porque inició la independencia de Chile.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (121, $v8$Reforzó el poder absoluto de los reyes europeos.$v8$, FALSE, 6),
    (122, $v8$Estableció que las colonias debían gobernarse desde España.$v8$, FALSE, 6),
    (123, $v8$Cuestionó la monarquía absoluta y difundió ideas de libertad e igualdad ante la ley.$v8$, TRUE, 6),
    (124, $v8$Impidió la difusión de nuevas ideas políticas en América.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (125, $v8$Porque fue el primer territorio americano en independizarse de España.$v8$, FALSE, 6),
    (126, $v8$Porque estableció nuevamente el dominio francés sobre América.$v8$, FALSE, 6),
    (127, $v8$Porque permitió que España recuperara el control del Caribe.$v8$, FALSE, 6),
    (128, $v8$Porque demostró que era posible terminar con el dominio colonial mediante una revolución.$v8$, TRUE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (129, $v8$Napoleón invadió España y el rey Fernando VII quedó apartado del trono, provocando una crisis de autoridad.$v8$, TRUE, 6),
    (130, $v8$España reconoció la independencia de todas sus colonias americanas.$v8$, FALSE, 6),
    (131, $v8$El rey Fernando VII decidió entregar el gobierno de América a los criollos.$v8$, FALSE, 6),
    (132, $v8$España estableció nuevas repúblicas en sus territorios americanos.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (133, $v8$Españoles nacidos en la península ibérica que ocupaban cargos administrativos.$v8$, FALSE, 6),
    (134, $v8$Personas extranjeras que llegaron a América después de la Independencia.$v8$, FALSE, 6),
    (135, $v8$Descendientes de españoles nacidos en América.$v8$, TRUE, 6),
    (136, $v8$Representantes de los pueblos originarios que gobernaban las colonias.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (137, $v8$Porque tenían prohibido participar en cualquier actividad económica.$v8$, FALSE, 6),
    (138, $v8$Porque, pese a tener riquezas e influencia, enfrentaban limitaciones para acceder a los principales cargos políticos.$v8$, TRUE, 6),
    (139, $v8$Porque España había eliminado todas las autoridades locales.$v8$, FALSE, 6),
    (140, $v8$Porque no podían vivir ni establecer propiedades en América.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (141, $v8$Entregar la independencia a las colonias americanas.$v8$, FALSE, 6),
    (142, $v8$Eliminar completamente los impuestos coloniales.$v8$, FALSE, 6),
    (143, $v8$Permitir que los criollos controlaran el gobierno de América.$v8$, FALSE, 6),
    (144, $v8$Fortalecer el control de la Corona española sobre sus territorios y aumentar sus ingresos.$v8$, TRUE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (145, $v8$La ausencia de un rey legítimo generó debates sobre quién debía gobernar los territorios americanos.$v8$, TRUE, 6),
    (146, $v8$La crisis permitió que España aumentara su control político sobre todas las colonias.$v8$, FALSE, 6),
    (147, $v8$Los movimientos americanos comenzaron únicamente por conflictos comerciales.$v8$, FALSE, 6),
    (148, $v8$La crisis provocó que todas las colonias fueran gobernadas por Francia.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (149, $v8$Porque Chile fue el único territorio americano que buscó independizarse.$v8$, FALSE, 6),
    (150, $v8$Porque todos los territorios americanos lograron su independencia en el mismo año.$v8$, FALSE, 6),
    (151, $v8$Porque distintas colonias americanas enfrentaron procesos de independencia relacionados con la crisis del dominio europeo.$v8$, TRUE, 6),
    (152, $v8$Porque los movimientos independentistas fueron organizados exclusivamente desde Chile.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (153, $v8$La Revolución francesa, la Ilustración y la crisis de la monarquía española.$v8$, TRUE, 6),
    (154, $v8$La Revolución Industrial, la Guerra del Pacífico y la Constitución de 1833.$v8$, FALSE, 6),
    (155, $v8$La fundación de la Universidad de Chile y la explotación del salitre.$v8$, FALSE, 6),
    (156, $v8$La colonización europea del sur de Chile y la cuestión social.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (157, $v8$Declaró inmediatamente la independencia definitiva de Chile.$v8$, FALSE, 6),
    (158, $v8$Estableció un gobierno local en Chile durante la ausencia del rey Fernando VII.$v8$, TRUE, 6),
    (159, $v8$Inició la Guerra del Pacífico contra Perú y Bolivia.$v8$, FALSE, 6),
    (160, $v8$Estableció una monarquía independiente dirigida por los criollos.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (161, $v8$Estableció un sistema parlamentario con escasas atribuciones presidenciales.$v8$, FALSE, 6),
    (162, $v8$Eliminó la autoridad del presidente de la República.$v8$, FALSE, 6),
    (163, $v8$Fortaleció el poder del presidente y estableció un gobierno republicano.$v8$, TRUE, 6),
    (164, $v8$Entregó el gobierno del país a autoridades elegidas por la Corona española.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (165, $v8$Dirigió la ocupación militar de la Araucanía.$v8$, FALSE, 6),
    (166, $v8$Fue uno de los principales responsables de la explotación del salitre.$v8$, FALSE, 6),
    (167, $v8$Participó en la organización de la Primera Junta Nacional de Gobierno.$v8$, FALSE, 6),
    (168, $v8$Fue el primer rector de la Universidad de Chile y contribuyó al desarrollo de la educación y la cultura.$v8$, TRUE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (169, $v8$La incorporación de territorios del norte, anteriormente pertenecientes a Perú y Bolivia.$v8$, TRUE, 6),
    (170, $v8$La pérdida definitiva de todos los territorios del norte del país.$v8$, FALSE, 6),
    (171, $v8$La incorporación de la Patagonia argentina al territorio chileno.$v8$, FALSE, 6),
    (172, $v8$La separación de Isla de Pascua del territorio nacional.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (173, $v8$La desaparición de las diferencias entre los grupos sociales.$v8$, FALSE, 6),
    (174, $v8$La eliminación de los problemas laborales en las ciudades.$v8$, FALSE, 6),
    (175, $v8$El crecimiento de la población trabajadora y el desarrollo de problemas asociados a las condiciones laborales y de vida.$v8$, TRUE, 6),
    (176, $v8$La disminución de las actividades económicas en las zonas mineras.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (177, $v8$La eliminación de las elecciones presidenciales.$v8$, FALSE, 6),
    (178, $v8$El reconocimiento del derecho de las mujeres a votar en las elecciones presidenciales y parlamentarias en 1949.$v8$, TRUE, 6),
    (179, $v8$La prohibición de la participación política de los ciudadanos.$v8$, FALSE, 6),
    (180, $v8$La reducción del acceso de la población a la educación.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (181, $v8$Se realizó el primer cabildo abierto de Chile.$v8$, FALSE, 6),
    (182, $v8$Se promulgó la Constitución de 1833.$v8$, FALSE, 6),
    (183, $v8$Se realizó el plebiscito que permitió iniciar el retorno a la democracia.$v8$, FALSE, 6),
    (184, $v8$Las Fuerzas Armadas y Carabineros derrocaron al gobierno del presidente Salvador Allende, iniciándose un régimen militar.$v8$, TRUE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (185, $v8$Permitió que continuara el régimen militar por un nuevo período presidencial.$v8$, FALSE, 6),
    (186, $v8$Estableció una nueva monarquía en Chile.$v8$, FALSE, 6),
    (187, $v8$La opción NO obtuvo la mayoría, lo que abrió el camino a elecciones presidenciales y parlamentarias y al retorno a un gobierno democrático.$v8$, TRUE, 6),
    (188, $v8$Eliminó la participación ciudadana en las decisiones políticas.$v8$, FALSE, 6);
INSERT INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES
    (189, $v8$La continuidad del uso del idioma español y el aumento del acceso de la población a la educación.$v8$, TRUE, 6),
    (190, $v8$La desaparición del idioma español y la eliminación de las escuelas.$v8$, FALSE, 6),
    (191, $v8$La permanencia de todas las formas de gobierno sin modificaciones.$v8$, FALSE, 6),
    (192, $v8$La ausencia de cambios en la participación política y en la vida cotidiana.$v8$, FALSE, 6);

-- 9. Vinculos alternativa-pregunta: 4 alternativas por pregunta.
INSERT INTO alternativa_pregunta (alternativa_pregunta_id, pregunta_id, alternativa_id)
SELECT i, ((i - 1) / 4) + 1, i FROM generate_series(1, 192) AS i;

-- 10. Vinculos pregunta-actividad (mismos ids que el seed mobile).
INSERT INTO pregunta_actividad (pregunta_actividad_id, actividad_id, pregunta_id) VALUES
    (1, 1, 1),
    (2, 1, 2),
    (3, 1, 3),
    (4, 2, 4),
    (5, 2, 5),
    (6, 2, 6),
    (7, 3, 7),
    (8, 3, 8),
    (9, 3, 9),
    (10, 4, 10),
    (11, 4, 11),
    (12, 4, 12),
    (13, 5, 13),
    (14, 5, 14),
    (15, 5, 15),
    (16, 6, 16),
    (17, 6, 17),
    (18, 6, 18),
    (19, 7, 19),
    (20, 7, 20),
    (21, 7, 21),
    (22, 7, 22),
    (23, 7, 23),
    (24, 7, 24),
    (25, 7, 25),
    (26, 7, 26),
    (27, 7, 27),
    (28, 7, 28),
    (29, 8, 29),
    (30, 8, 30),
    (31, 8, 31),
    (32, 8, 32),
    (33, 8, 33),
    (34, 9, 34),
    (35, 9, 35),
    (36, 9, 36),
    (37, 9, 37),
    (38, 9, 38),
    (39, 10, 39),
    (40, 10, 40),
    (41, 10, 41),
    (42, 10, 42),
    (43, 10, 43),
    (44, 10, 44),
    (45, 10, 45),
    (46, 10, 46),
    (47, 10, 47),
    (48, 10, 48),
    (49, 11, 1),
    (50, 11, 2),
    (51, 11, 3),
    (52, 11, 4),
    (53, 11, 5),
    (54, 11, 6),
    (55, 12, 7),
    (56, 12, 8),
    (57, 12, 9),
    (58, 12, 10),
    (59, 12, 11),
    (60, 12, 12);

-- 11. Intento cerrado de ejemplo (estudiante 6, curso 1, actividad 5 de
--     Matematica) para que el informe preliminar de la unidad 2 tenga datos.
INSERT INTO intento (intento_id, fecha_inicio, fecha_fin, usuario_id, actividad_curso_id) VALUES
    (1, '2026-11-01T10:00:00Z', '2026-11-01T10:15:00Z', 6, 9);

INSERT INTO respuesta (respuesta_id, alternativa_id, pregunta_id, intento_id) VALUES
    (1, 50, 13, 1),
    (2, 53, 14, 1),
    (3, 60, 15, 1);

-- 12. Reajustar secuencias de las tablas reconstruidas.
DO $$
DECLARE
    tabla text;
    columna text;
    maximo bigint;
    tiene_filas boolean;
BEGIN
    FOR tabla, columna IN
        SELECT * FROM (VALUES
            ('contenido', 'contenido_id'),
            ('objetivo_aprendizaje_contenido', 'oa_contenido_id'),
            ('recurso', 'recurso_id'),
            ('actividad', 'actividad_id'),
            ('actividad_curso', 'actividad_curso_id'),
            ('pregunta', 'pregunta_id'),
            ('alternativa', 'alternativa_id'),
            ('alternativa_pregunta', 'alternativa_pregunta_id'),
            ('pregunta_actividad', 'pregunta_actividad_id'),
            ('intento', 'intento_id'),
            ('respuesta', 'respuesta_id')
        ) AS t(tabla, columna)
    LOOP
        EXECUTE format('SELECT COALESCE(MAX(%I), 0), COUNT(*) > 0 FROM %I', columna, tabla)
            INTO maximo, tiene_filas;
        PERFORM setval(
            pg_get_serial_sequence(tabla, columna),
            GREATEST(maximo, 1),
            tiene_filas
        );
    END LOOP;
END
$$;

-- 13. Verificacion final: si el resultado no coincide con el mobile, se aborta.
DO $$
DECLARE
    n_contenidos integer;
    n_actividades integer;
    n_pruebas_finales integer;
    n_preguntas integer;
    n_alternativas integer;
    n_pregunta_actividad integer;
    n_alternativa_pregunta integer;
BEGIN
    SELECT COUNT(*) INTO n_contenidos FROM contenido;
    SELECT COUNT(*) INTO n_actividades FROM actividad;
    SELECT COUNT(*) INTO n_pruebas_finales FROM actividad WHERE tipo_actividad_id = 3;
    SELECT COUNT(*) INTO n_preguntas FROM pregunta;
    SELECT COUNT(*) INTO n_alternativas FROM alternativa;
    SELECT COUNT(*) INTO n_pregunta_actividad FROM pregunta_actividad;
    SELECT COUNT(*) INTO n_alternativa_pregunta FROM alternativa_pregunta;

    IF n_contenidos <> 6 OR n_actividades <> 12 OR n_pruebas_finales <> 4
            OR n_preguntas <> 48 OR n_alternativas <> 192
            OR n_pregunta_actividad <> 60 OR n_alternativa_pregunta <> 192 THEN
        RAISE EXCEPTION
            'V8 inconsistente: contenidos=%, actividades=%, pruebas_finales=%, preguntas=%, alternativas=%, pregunta_actividad=%, alternativa_pregunta=%',
            n_contenidos, n_actividades, n_pruebas_finales, n_preguntas,
            n_alternativas, n_pregunta_actividad, n_alternativa_pregunta;
    END IF;
END
$$;
