# Entidades y atributos

1. **Rol**

   * `id_rol`
   * `nombre`

2. **Usuario**

   * `id_usuario`
   * `nombre`
   * `correo`
   * `password`
   * `id_rol`

3. **Curso**

   * `id_curso`
   * `nombre`
   * `nivel`
   * `anio`

4. **UsuarioCurso**

   * `id_usuario`
   * `id_curso`

5. **Asignatura**

   * `id_asignatura`
   * `nombre`

6. **Unidad**

   * `id_unidad`
   * `id_asignatura`
   * `numero`
   * `nombre`
   * `estado`

7. **OA**

   * `id_oa`
   * `id_unidad`
   * `codigo`
   * `eje`
   * `texto_referencia`

8. **CursoOA**

   * `id_curso_oa`
   * `id_curso`
   * `id_oa`

9. **Contenido**

   * `id_contenido`
   * `titulo`
   * `descripcion`
   * `estado`
   * `origen`

10. **ContenidoOA**

    * `id_contenido`
    * `id_oa`

11. **Desafio**

    * `id_desafio`
    * `nombre`
    * `descripcion`
    * `nivel_dificultad`

12. **Actividad**

    * `id_actividad`
    * `id_contenido`
    * `id_desafio`
    * `titulo`
    * `instrucciones`
    * `orden`
    * `tipo`

13. **Pregunta**

    * `id_pregunta`
    * `enunciado`
    * `tipo`

14. **PreguntaActividad**

    * `id_pregunta`
    * `id_actividad`

15. **Alternativa**

    * `id_alternativa`
    * `id_pregunta`
    * `texto`
    * `es_correcta`

16. **Asignacion**

    * `id_asignacion`
    * `id_curso`
    * `id_contenido`
    * `id_desafio`
    * `fecha_asignacion`

17. **Progreso**

    * `id_progreso`
    * `id_usuario`
    * `id_asignacion`
    * `estado`
    * `fecha_inicio`
    * `fecha_completado`

18. **RespuestaActividad**

    * `id_respuesta`
    * `id_usuario`
    * `id_pregunta`
    * `id_alternativa`
    * `es_correcta`
    * `fecha_respuesta`

19. **PruebaFinal**

    * `id_prueba`
    * `id_curso`
    * `id_unidad`
    * `limite_intentos`
    * `habilitada`

20. **PreguntaPrueba**

    * `id_pregunta`
    * `id_prueba`
    * `orden`

21. **IntentoPrueba**

    * `id_intento`
    * `id_prueba`
    * `id_estudiante`
    * `numero_intento`
    * `fecha_inicio`
    * `fecha_termino`
    * `estado`

22. **RespuestaPrueba**

    * `id_respuesta`
    * `id_intento`
    * `id_pregunta`
    * `id_alternativa`
    * `es_correcta`

23. **Informe**

    * `id_informe`
    * `id_estudiante`
    * `id_curso`
    * `id_asignatura`
    * `id_unidad`
    * `id_docente`
    * `fecha_generacion`
    * `estado`

24. **InformeOA**

    * `id_informe_oa`
    * `id_informe`
    * `id_oa`
    * `porcentaje_aciertos`
    * `clasificacion`

# Relaciones

1. Rol 1:N Usuario
2. Usuario N:M Curso mediante UsuarioCurso
3. Asignatura 1:N Unidad
4. Unidad 1:N OA
5. Curso N:M OA mediante CursoOA
6. Contenido N:M OA mediante ContenidoOA
7. Contenido 1:N Actividad
8. Desafio 1:N Actividad
9. Actividad N:M Pregunta mediante PreguntaActividad
10. Pregunta 1:N Alternativa
11. Curso 1:N Asignacion
12. Contenido 1:N Asignacion
13. Desafio 1:N Asignacion
14. Usuario 1:N Progreso
15. Asignacion 1:N Progreso
16. Usuario 1:N RespuestaActividad
17. Pregunta 1:N RespuestaActividad
18. Alternativa 1:N RespuestaActividad
19. Curso 1:N PruebaFinal
20. Unidad 1:N PruebaFinal
21. PruebaFinal N:M Pregunta mediante PreguntaPrueba
22. PruebaFinal 1:N IntentoPrueba
23. Usuario 1:N IntentoPrueba
24. IntentoPrueba 1:N RespuestaPrueba
25. Pregunta 1:N RespuestaPrueba
26. Alternativa 1:N RespuestaPrueba
27. Usuario 1:N Informe como estudiante
28. Curso 1:N Informe
29. Asignatura 1:N Informe
30. Unidad 1:N Informe
31. Usuario 1:N Informe como docente
32. Informe 1:N InformeOA
33. OA 1:N InformeOA
