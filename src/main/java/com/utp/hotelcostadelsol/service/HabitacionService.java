package com.utp.hotelcostadelsol.service;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.utp.hotelcostadelsol.dao.HabitacionDAO;
import com.utp.hotelcostadelsol.dao.IHabitacionDAO;
import com.utp.hotelcostadelsol.dao.TipoHabitacionDAO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para la gestion del inventario de habitaciones.
 * Incorpora Google Guava CacheBuilder para optimizar lecturas del catalogo de tipos,
 * Preconditions, Apache Commons (Lang3 y Collections4), Logback e inyeccion SOLID.
 */
public class HabitacionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HabitacionService.class);
    private static final String CACHE_KEY_TIPOS = "CATALOGO_TIPOS_HABITACION";

    private final IHabitacionDAO habitacionDAO;
    private final TipoHabitacionDAO tipoHabitacionDAO;
    private final Cache<String, List<TipoHabitacionDTO>> catalogoCache;

    public HabitacionService() {
        this(new HabitacionDAO(), new TipoHabitacionDAO());
    }

    public HabitacionService(IHabitacionDAO habitacionDAO, TipoHabitacionDAO tipoHabitacionDAO) {
        this.habitacionDAO = Preconditions.checkNotNull(habitacionDAO, "IHabitacionDAO requerido");
        this.tipoHabitacionDAO = Preconditions.checkNotNull(tipoHabitacionDAO, "TipoHabitacionDAO requerido");
        this.catalogoCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public List<HabitacionDTO> listarTodas() {
        return habitacionDAO.findAll();
    }

    public List<HabitacionDTO> listarDisponibles() {
        return habitacionDAO.findAll().stream()
                .filter(h -> "DISPONIBLE".equalsIgnoreCase(h.getEstado()))
                .collect(Collectors.toList());
    }

    public List<HabitacionDTO> listarPorFiltro(String estado, Integer idTipo) {
        return habitacionDAO.findByFiltro(StringUtils.trimToEmpty(estado), idTipo);
    }

    /**
     * Obtiene el catalogo de tipos de habitacion utilizando la cache de Google Guava.
     *
     * @return lista de TipoHabitacionDTO
     */
    public List<TipoHabitacionDTO> listarTipos() {
        List<TipoHabitacionDTO> cached = catalogoCache.getIfPresent(CACHE_KEY_TIPOS);
        if (CollectionUtils.isNotEmpty(cached)) {
            return cached;
        }
        List<TipoHabitacionDTO> loaded = tipoHabitacionDAO.findAll();
        if (CollectionUtils.isNotEmpty(loaded)) {
            catalogoCache.put(CACHE_KEY_TIPOS, loaded);
        }
        return loaded;
    }

    public boolean registrarHabitacion(HabitacionDTO h) {
        if (h == null || StringUtils.isBlank(h.getNumero())) {
            LOGGER.warn("Intento de registrar habitacion con numero vacio.");
            return false;
        }
        h.setNumero(StringUtils.trimToEmpty(h.getNumero()));
        h.setEstado(StringUtils.defaultIfBlank(h.getEstado(), "DISPONIBLE").toUpperCase());
        boolean saved = habitacionDAO.save(h);
        if (saved) {
            LOGGER.info("Habitacion {} registrada exitosamente en el inventario.", h.getNumero());
        }
        return saved;
    }

    public boolean actualizarEstado(int idHabitacion, String nuevoEstado) {
        Preconditions.checkArgument(idHabitacion > 0, "El ID de habitacion debe ser positivo");
        Preconditions.checkArgument(StringUtils.isNotBlank(nuevoEstado), "El nuevo estado es obligatorio");
        return habitacionDAO.updateEstado(idHabitacion, nuevoEstado.trim().toUpperCase());
    }

    public Map<String, Integer> obtenerResumenEstados() {
        return habitacionDAO.contarPorEstado();
    }
}
