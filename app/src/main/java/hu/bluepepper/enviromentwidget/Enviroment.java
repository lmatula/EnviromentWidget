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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enviroment that = (Enviroment) o;

        if (Float.compare(that.hum, hum) != 0) return false;
        if (Float.compare(that.humMax, humMax) != 0) return false;
        if (Float.compare(that.humMin, humMin) != 0) return false;
        if (Float.compare(that.temp, temp) != 0) return false;
        if (Float.compare(that.tempMax, tempMax) != 0) return false;
        if (Float.compare(that.tempMin, tempMin) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (temp != +0.0f ? Float.floatToIntBits(temp) : 0);
        result = 31 * result + (hum != +0.0f ? Float.floatToIntBits(hum) : 0);
        result = 31 * result + (humMin != +0.0f ? Float.floatToIntBits(humMin) : 0);
        result = 31 * result + (humMax != +0.0f ? Float.floatToIntBits(humMax) : 0);
        result = 31 * result + (tempMin != +0.0f ? Float.floatToIntBits(tempMin) : 0);
        result = 31 * result + (tempMax != +0.0f ? Float.floatToIntBits(tempMax) : 0);
        return result;
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
