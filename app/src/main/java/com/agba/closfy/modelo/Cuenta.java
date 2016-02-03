package com.agba.closfy.modelo;

public class Cuenta {
	String idCuenta;
	String descCuenta;
	int sexo;
	int idIcon;

	public String getIdCuenta() {
		return idCuenta;
	}
	public void setIdCuenta(String idCuenta) {
		this.idCuenta = idCuenta;
	}
	public String getDescCuenta() {
		return descCuenta;
	}
	public void setDescCuenta(String descCuenta) {
		this.descCuenta = descCuenta;
	}

	public int getIdIcon() {
		return idIcon;
	}

	public void setIdIcon(int idIcon) {
		this.idIcon = idIcon;
	}

	public int getSexo() {
		return sexo;
	}

	public void setSexo(int sexo) {
		this.sexo = sexo;
	}
}
