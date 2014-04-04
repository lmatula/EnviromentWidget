package hu.bluepepper.enviromentwidget;

public class Enviroment {
	private float temp;
	private float hum;
	private float humMin;
	private float humMax;
	private float tempMin;
	private float tempMax;
	
	Enviroment() {
		
	}
	
	Enviroment(float temp, float minTemp, float maxTemp, float hum, float minHum, float maxHum) {
		this.temp = temp;
		this.tempMin = minTemp;
		this.tempMax = maxTemp;
		this.hum = hum;
		this.humMin = minHum;
		this.humMax = maxHum;
	}
	
	public float getTemp() {
		return temp;
	}
	public void setTemp(float temp) {
		this.temp = temp;
	}
	public float getHum() {
		return hum;
	}
	public void setHum(float hum) {
		this.hum = hum;
	}
	public float getHumMin() {
		return humMin;
	}
	public void setHumMin(float humMin) {
		this.humMin = humMin;
	}
	public float getHumMax() {
		return humMax;
	}
	public void setHumMax(float humMax) {
		this.humMax = humMax;
	}
	public float getTempMin() {
		return tempMin;
	}
	public void setTempMin(float tempMin) {
		this.tempMin = tempMin;
	}
	public float getTempMax() {
		return tempMax;
	}
	public void setTempMax(float tempMax) {
		this.tempMax = tempMax;
	}
}
