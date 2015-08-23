package com.agba.closfy.modelo;

import android.graphics.Typeface;

public class ViewsVo {
	private int viewId, color, style, viewHeight, pos, idPrenda;
	private String text;
	private float size, ancho, alto,  xValue, yValue ;
	private Typeface face;

	public float getxValue() {
		return xValue;
	}

	public void setxValue(float xValue) {
		this.xValue = xValue;
	}

	public float getyValue() {
		return yValue;
	}

	public void setyValue(float yValue) {
		this.yValue = yValue;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public Typeface getFace() {
		return face;
	}

	public void setFace(Typeface face) {
		this.face = face;
	}

	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getIdPrenda() {
		return idPrenda;
	}

	public void setIdPrenda(int idPrenda) {
		this.idPrenda = idPrenda;
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

}
