package com.agba.closfy.modelo;

import android.graphics.drawable.Drawable;


public class Look {

	private int idLook;
	private int idTemporada;
	private int favorito;
	private Prenda prendaSup;
	private Prenda prendaInf;
	private Prenda prendaCuerpo;
	private Prenda prendaAbrigo;
	private Prenda prendaCalzado;
	private Prenda prendaCompl1;
	private Prenda prendaCompl2;
	private String cadenaPrendas;
	private String utilidades;
	private String notas;
	private String idFoto;
	private Drawable foto;
	private String colorFondo;

	public int getIdLook() {
		return idLook;
	}

	public void setIdLook(int idLook) {
		this.idLook = idLook;
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

	public Prenda getPrendaSup() {
		return prendaSup;
	}

	public void setPrendaSup(Prenda prendaSup) {
		this.prendaSup = prendaSup;
	}

	public Prenda getPrendaInf() {
		return prendaInf;
	}

	public void setPrendaInf(Prenda prendaInf) {
		this.prendaInf = prendaInf;
	}

	public Prenda getPrendaCuerpo() {
		return prendaCuerpo;
	}

	public void setPrendaCuerpo(Prenda prendaCuerpo) {
		this.prendaCuerpo = prendaCuerpo;
	}

	public Prenda getPrendaAbrigo() {
		return prendaAbrigo;
	}

	public void setPrendaAbrigo(Prenda prendaAbrigo) {
		this.prendaAbrigo = prendaAbrigo;
	}

	public Prenda getPrendaCalzado() {
		return prendaCalzado;
	}

	public void setPrendaCalzado(Prenda prendaCalzado) {
		this.prendaCalzado = prendaCalzado;
	}

	public Prenda getPrendaCompl1() {
		return prendaCompl1;
	}

	public void setPrendaCompl1(Prenda prendaCompl1) {
		this.prendaCompl1 = prendaCompl1;
	}

	public Prenda getPrendaCompl2() {
		return prendaCompl2;
	}

	public void setPrendaCompl2(Prenda prendaCompl2) {
		this.prendaCompl2 = prendaCompl2;
	}

	public String getCadenaPrendas() {
		return cadenaPrendas;
	}

	public void setCadenaPrendas(String cadenaPrendas) {
		this.cadenaPrendas = cadenaPrendas;
	}

	public String getUtilidades() {
		return utilidades;
	}

	public void setUtilidades(String utilidades) {
		this.utilidades = utilidades;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public String getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(String idFoto) {
		this.idFoto = idFoto;
	}

	public String getColorFondo() {
		return colorFondo;
	}

	public void setColorFondo(String colorFondo) {
		this.colorFondo = colorFondo;
	}

	public Drawable getFoto() {
		return foto;
	}

	public void setFoto(Drawable foto) {
		this.foto = foto;
	}
	
	
}
