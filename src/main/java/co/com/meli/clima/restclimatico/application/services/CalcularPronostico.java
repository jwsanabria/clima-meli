package co.com.meli.clima.restclimatico.application.services;


import co.com.meli.clima.restclimatico.application.dto.CondicionesDiarias;
import co.com.meli.clima.restclimatico.application.dto.Coordenada;
import co.com.meli.clima.restclimatico.application.dto.CoordenadasDiarias;
import co.com.meli.clima.restclimatico.application.dto.InfoPlanetaria;
import co.com.meli.clima.restclimatico.domain.entity.Planeta;
import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import co.com.meli.clima.restclimatico.infrastructure.repository.PlanetaRepository;
import co.com.meli.clima.restclimatico.infrastructure.repository.PronosticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CalcularPronostico {

    private static final Logger log = LoggerFactory.getLogger(CalcularPronostico.class);

    private Planeta planetaRef;

    @Autowired
    private PlanetaRepository planetaRepository;


    @Autowired
    private PronosticoRepository pronosticoRepository;


    private int calcularDias(Planeta planeta, int anios){
        return 360/planeta.getVelocidad() * anios;
    }

    private void registrarPronostico(Map<Integer, CondicionesDiarias> condicionesDiarias){
        for (Map.Entry<Integer, CondicionesDiarias> entry : condicionesDiarias.entrySet()) {
            Pronostico pronostico = new Pronostico();
            pronostico.setDia(entry.getKey());
            CondicionesDiarias cond = entry.getValue();
            String clima = "Normal";
            if(cond.isSequia()) {
                clima = "Sequia";
            }else if(cond.isLluvia()){
                clima = "Lluvia";
            }else if(cond.isOptimas()){
                clima = "Optimas condiciones de presión y temperatura";
            }

            pronostico.setClima(clima);

            pronosticoRepository.save(pronostico);
        }

    }

    private void planetaReferencia(Integer idPlaneta) {
        Planeta planeta = planetaRepository.findById(idPlaneta);
        if(planeta!=null){
            planetaRef = planeta;
        }
    }

    private InfoPlanetaria obtenerInfoPlanetaria(Planeta planeta, int diasRef){
        Map<Integer, CoordenadasDiarias> mapDiario = new HashMap<>();
        int dias = 360/planeta.getVelocidad();
        int dia = 1;
        for(int i = 1; i<=diasRef; i=i+dias){
            for(int j = 1; j<=dias; j++){
                int angulo = planeta.getHorario().booleanValue()? j * planeta.getVelocidad() : 360 - (j * planeta.getVelocidad());
                Coordenada coord = new Coordenada(planeta.getRadio() * Math.round(Math.cos(angulo)),
                        planeta.getRadio() * Math.round(Math.sin(angulo)));

                CoordenadasDiarias diaria = new CoordenadasDiarias();
                diaria.setDia(dia);
                diaria.setCoordenada(coord);
                diaria.setAngulo(angulo);

                mapDiario.put(Integer.valueOf(dia), diaria);
                dia++;
            }
        }

        InfoPlanetaria infoPlanetaria = new InfoPlanetaria();
        infoPlanetaria.setPlaneta(planeta);
        infoPlanetaria.setMovimientosDiarios(mapDiario);
        return infoPlanetaria;
    }


    private Map<String, InfoPlanetaria> obtenerInformacionSistemaSolar(int diasRef) throws Exception{
        Map<String, InfoPlanetaria> sistemaSolar = new HashMap<>();
        Iterable<Planeta> results =  planetaRepository.findAll();
        if(results!=null){
            Iterator<Planeta> it = results.iterator();
            while(it.hasNext()){
                Planeta planeta = it.next();
                sistemaSolar.put(planeta.getNombre(), obtenerInfoPlanetaria(planeta, diasRef));
            }
        }else{
            log.error("No hay datos");
            throw new Exception("No se encuentran datos de los planetas");
        }

        return sistemaSolar;
    }

    private Boolean determinarSequia(Map<String, InfoPlanetaria> sistemaSolar, InfoPlanetaria infoPlanetariaRef, int dia){

        Boolean sequia = true;

        for (Map.Entry<String, InfoPlanetaria> entry : sistemaSolar.entrySet()) {
            int anguloRef = infoPlanetariaRef.getMovimientosDiarios().get(Integer.valueOf(dia)).getAngulo();
            int anguloPlaneta = entry.getValue().getMovimientosDiarios().get(Integer.valueOf(dia)).getAngulo();
            int anguloPlanetaInv = anguloPlaneta + 180 > 360 ? (anguloPlaneta + 180) - 360 : anguloPlaneta + 180;

            if (anguloRef != anguloPlaneta && anguloRef != anguloPlanetaInv) {
                sequia = false;
                break;
            }
        }

        return sequia;
    }


    private Boolean determinarLluvia(Coordenada coordRef, Coordenada coordPuntoA, Coordenada coordPuntoB){
        Boolean lluvia = false;

        Double perimetro = calcularPerimetro(coordPuntoA, coordPuntoB, coordRef);
        Coordenada vectorD = calcularVector(coordRef, coordPuntoA);
        Coordenada vectorE = calcularVector(coordRef, coordPuntoB);
        Double w1 = calcularPonderancion1(vectorD, vectorE, coordRef, new Coordenada(0, 0));
        Double w2 = calcularPonderacion2(w1, vectorD, vectorE, coordRef, new Coordenada(0, 0));

        if (w1 >= 0 && w2 >= 0 && w1 + w2 <= 1) {
            lluvia = true;
        }

        return lluvia;
    }


    private Boolean determinarCondOptimas(Coordenada coordRef, Coordenada coordPuntoA, Coordenada coordPuntoB){
        Boolean condOptimas = false;
        Double pendiente = calcularPendiente(coordRef, coordPuntoA);
        Double complemento = complemento(coordRef, pendiente);
        condOptimas = validarPuntoEnRecta(coordPuntoB, pendiente, complemento);

        return condOptimas;
    }

    private void eliminarData(){
        Iterable<Pronostico> it = pronosticoRepository.findAll();
        while(it.iterator().hasNext()){
            Pronostico p = it.iterator().next();
            pronosticoRepository.delete(p);
        }
    }

    public void realizarPronostico(Integer idPlaneta, int anios) throws Exception{
        eliminarData();
        planetaReferencia(idPlaneta);
        int diasRef = calcularDias(planetaRef, anios);

        Map<String, InfoPlanetaria> sistemaSolar = obtenerInformacionSistemaSolar(diasRef);

        InfoPlanetaria infoPlanetariaRef = sistemaSolar.get(planetaRef.getNombre());
        sistemaSolar.remove(planetaRef.getNombre());


        Map<Integer, CondicionesDiarias> mapCondiciones = new HashMap<>();
        for(int i = 1; i<=diasRef; i++) {
            mapCondiciones.put(Integer.valueOf(i), new CondicionesDiarias());

            Boolean sequia = determinarSequia(sistemaSolar, infoPlanetariaRef, i);

            List<InfoPlanetaria> listPlanetas = new ArrayList<>(sistemaSolar.values());

            Coordenada coordRef = infoPlanetariaRef.getMovimientosDiarios().get(Integer.valueOf(i)).getCoordenada();
            Coordenada coordPuntoA = listPlanetas.get(0).getMovimientosDiarios().get(Integer.valueOf(i)).getCoordenada();
            Coordenada coordPuntoB = listPlanetas.get(1).getMovimientosDiarios().get(Integer.valueOf(i)).getCoordenada();

            Boolean lluvia = determinarLluvia(coordRef, coordPuntoA, coordPuntoB);

            Boolean condOptimas = determinarCondOptimas(coordRef, coordPuntoA, coordPuntoB);

            mapCondiciones.get(Integer.valueOf(i)).setSequia(sequia);
            mapCondiciones.get(Integer.valueOf(i)).setLluvia(lluvia);
            mapCondiciones.get(Integer.valueOf(i)).setOptimas(condOptimas);

        }

        registrarPronostico(mapCondiciones);
    }

    private Coordenada calcularVector(Coordenada coord1, Coordenada coord2){
        Coordenada resultado = new Coordenada(0.0, 0.0);
        resultado.setX(coord2.getX() - coord1.getX());
        resultado.setY(coord2.getY() - coord1.getY());

        return resultado;
    }

    private Double calcularPonderancion1(Coordenada vectorD, Coordenada vectorE, Coordenada puntoOrigen, Coordenada punto){
        Double resultado;
        try{
            resultado = vectorE.getX()*(puntoOrigen.getY()-punto.getY()) + vectorE.getY() * (punto.getX()- puntoOrigen.getX());
            resultado = resultado / (vectorD.getX()*vectorE.getY() - vectorD.getY()*vectorE.getX());
        }catch(ArithmeticException ex){
            resultado = 0.0;
            log.warn("Ponderacion 1 por cero!");
        }

        return resultado;
    }

    private Double calcularPonderacion2(Double w1, Coordenada vectorD, Coordenada vectorE, Coordenada puntoOrigen, Coordenada punto){
        Double resultado;
        try{
            resultado = (punto.getY()- puntoOrigen.getY() - w1 * vectorD.getY())+vectorE.getY();
        }catch(ArithmeticException ex){
            resultado = 0.0;
            log.warn("Ponderacion 2 por cero!");
        }

        return resultado;
    }

    private Double calcularPerimetro(Coordenada puntoA, Coordenada puntoB, Coordenada puntoC){
        Double resultado = Math.sqrt(Math.pow(puntoA.getX()-puntoB.getX(), 2)+Math.pow(puntoA.getY()-puntoB.getY(), 2));
        resultado += Math.sqrt(Math.pow(puntoB.getX()-puntoC.getX(), 2)+Math.pow(puntoB.getY()-puntoC.getY(), 2));
        resultado += Math.sqrt(Math.pow(puntoC.getX()-puntoA.getX(), 2)+Math.pow(puntoC.getY()-puntoA.getY(), 2));
        return resultado;
    }

    private Double calcularPendiente(Coordenada puntoA, Coordenada puntoB){
        Double resultado = 0.0;

        try{
            resultado = (puntoB.getY()- puntoA.getY())/(puntoB.getX() - puntoA.getX());
        }catch (ArithmeticException ex){
            log.error("Error en pendiente");
        }

        return resultado;
    }

    private Double complemento(Coordenada coord, Double pendiente){
        return coord.getY()-(pendiente * coord.getX());
    }

    private Boolean validarPuntoEnRecta(Coordenada punto, Double pendiente, Double complemento){
        Boolean resultado = false;

        if(pendiente * punto.getX() + complemento == punto.getY()){
            resultado = true;
        }

        return resultado;
    }
}

