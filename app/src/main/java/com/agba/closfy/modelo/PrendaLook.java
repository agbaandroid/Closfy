package com.agba.closfy.modelo;

import android.graphics.drawable.Drawable;

public class PrendaLook {
	int idLook;
	int idPrenda;
	int idTipo;
	int prendaBasica;
	int idPrendaBasica;	
	float posiX;
	float posiY;
	float ancho;
	float alto;
	int pos;
	String idFoto;
	Drawable foto;
	
	public int getIdLook() {
		return idLook;
	}
	public void setIdLook(int idLook) {
		this.idLook = idLook;
	}
	public int getIdPrenda() {
		return idPrenda;
	}
	public void setIdPrenda(int idPrenda) {
		this.idPrenda = idPrenda;
	}
	public float getPosiX() {
		return posiX;
	}
	public void setPosiX(float posiX) {
		this.posiX = posiX;
	}
	public float getPosiY() {
		return posiY;
	}
	public void setPosiY(float posiY) {
		this.posiY = posiY;
	}
	public float getAncho() {
		return ancho;
	}
	public void setAncho(float ancho) {
		this.ancho = ancho;
	}
	public float getAlto() {
		return alto;
	}
	public void setAlto(float alto) {
		this.alto = alto;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
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
	public int getIdTipo() {
		return idTipo;
	}
	public void setIdTipo(int idTipo) {
		this.idTipo = idTipo;
	}
	
	
}
