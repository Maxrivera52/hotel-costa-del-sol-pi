package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO que representa un pago o verificacion de comprobante/voucher asociado a una reserva.
 */
public class PagoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Integer idPago;
    private Integer idReserva;
    private String codigoReserva;
    private BigDecimal monto;
    private String metodoPago;
    private String numeroOperacion;
    private String comprobanteVoucher;
    private String estadoPago;
    private Integer idRecepcionista;
    private LocalDateTime fechaPago;

    public PagoDTO() {
        this.monto = BigDecimal.ZERO;
        this.metodoPago = "EFECTIVO_VENTANILLA";
        this.estadoPago = "VERIFICADO";
        this.fechaPago = LocalDateTime.now();
    }

    public PagoDTO(Integer idPago, Integer idReserva, String codigoReserva, BigDecimal monto,
                   String metodoPago, String numeroOperacion, String comprobanteVoucher,
                   String estadoPago, Integer idRecepcionista, LocalDateTime fechaPago) {
        this.idPago = idPago;
        this.idReserva = idReserva;
        this.codigoReserva = codigoReserva;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.numeroOperacion = numeroOperacion;
        this.comprobanteVoucher = comprobanteVoucher;
        this.estadoPago = estadoPago;
        this.idRecepcionista = idRecepcionista;
        this.fechaPago = fechaPago != null ? fechaPago : LocalDateTime.now();
    }

    public String getFechaPagoFormateada() {
        return fechaPago != null ? fechaPago.format(FMT) : "";
    }

    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }
    public String getCodigoReserva() { return codigoReserva; }
    public void setCodigoReserva(String codigoReserva) { this.codigoReserva = codigoReserva; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }
    public String getComprobanteVoucher() { return comprobanteVoucher; }
    public void setComprobanteVoucher(String comprobanteVoucher) { this.comprobanteVoucher = comprobanteVoucher; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public Integer getIdRecepcionista() { return idRecepcionista; }
    public void setIdRecepcionista(Integer idRecepcionista) { this.idRecepcionista = idRecepcionista; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}
