package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Modelo.
 */
@Entity
@Table(name = "modelo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Modelo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "modelo", length = 50, nullable = false)
    private String modelo;

    @Column(name = "color")
    private String color;

    @Column(name = "potencia")
    private Integer potencia;

    @Column(name = "plazas")
    private Integer plazas;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "numero_serie")
    private String numero_serie;

    public String getNumero_serie() {
        return numero_serie;
    }

    public void setNumero_serie(String numero_serie) {
        this.numero_serie = numero_serie;
    }

    @ManyToOne
    private Marca marca;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Modelo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelo() {
        return this.modelo;
    }

    public Modelo modelo(String modelo) {
        this.setModelo(modelo);
        return this;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return this.color;
    }

    public Modelo color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getPotencia() {
        return this.potencia;
    }

    public Modelo potencia(Integer potencia) {
        this.setPotencia(potencia);
        return this;
    }

    public void setPotencia(Integer potencia) {
        this.potencia = potencia;
    }

    public Integer getPlazas() {
        return this.plazas;
    }

    public Modelo plazas(Integer plazas) {
        this.setPlazas(plazas);
        return this;
    }

    public void setPlazas(Integer plazas) {
        this.plazas = plazas;
    }

    public Double getPrecio() {
        return this.precio;
    }

    public Modelo precio(Double precio) {
        this.setPrecio(precio);
        return this;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Marca getMarca() {
        return this.marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo marca(Marca marca) {
        this.setMarca(marca);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Modelo)) {
            return false;
        }
        return id != null && id.equals(((Modelo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Modelo{" +
            "id=" + getId() +
            ", modelo='" + getModelo() + "'" +
            ", color='" + getColor() + "'" +
            ", potencia=" + getPotencia() +
            ", plazas=" + getPlazas() +
            ", precio=" + getPrecio() +
            "}";
    }
}
