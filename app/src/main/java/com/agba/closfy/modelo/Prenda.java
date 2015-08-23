package com.agba.closfy.modelo;

import android.graphics.drawable.Drawable;

public class Prenda {

	private int idPrenda;
	private int idTipo;
	private int prendaBasica;
	private int idPrendaBasica;
	private int idTemporada;
	private int favorito;
	private String idFoto;
	private Drawable foto;
	private String utilidades;

	public int getIdPrenda() {
		return idPrenda;
	}

	public void setIdPrenda(int idPrenda) {
		this.idPrenda = idPrenda;
	}

	public int getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(int idTipo) {
		this.idTipo = idTipo;
	}

	public int getIdTemporada() {
		return idTemporada;
	}

	public void setIdTemporada(int idTemporada) {
		this.idTemporada = idTemporada;
	}

	public int getFavorito() {
		return favorito;
	}

	public void setFavorito(int favorito) {
		this.favorito = favorito;
	}

	public String getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(String idFoto) {
		this.idFoto = idFoto;
	}

	public Drawable getFoto() {
		return foto;
	}

	public void setFoto(Drawable foto) {
		this.foto = foto;
	}

	public String getUtilidades() {
		return utilidades;
	}

	public void setUtilidades(String utilidades) {
		this.utilidades = utilidades;
	}

	public int getPrendaBasica() {
		return prendaBasica;
	}

	public void setPrendaBasica(int prendaBasica) {
		this.prendaBasica = prendaBasica;
	}

	public int getIdPrendaBasica() {
		return idPrendaBasica;
	}

	public void setIdPrendaBasica(int idPrendaBasica) {
		this.idPrendaBasica = idPrendaBasica;
	}

}
