package game.demos;

public class Planet {
	private String name;
	private double radius;
	private double hillsHeight;
	private double hillsRate;
	
	public Planet(double radius, double hillsHeight, double hillsRate) {
		super();
		name = "Default planet";
		this.radius = radius;
		this.hillsHeight = hillsHeight;
		this.hillsRate = hillsRate;
	}
	public Planet(String name, double radius, double hillsHeight, double hillsRate) {
		super();
		this.name = name;
		this.radius = radius;
		this.hillsHeight = hillsHeight;
		this.hillsRate = hillsRate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
		if(this.radius<0)this.radius=0;
	}
	public double getHillsHeight() {
		return hillsHeight;
	}
	public void setHillsHeight(double hillsHeight) {
		this.hillsHeight = hillsHeight;
	}
	public double getHillsRate() {
		return hillsRate;
	}
	public void setHillsRate(double hillsRate) {
		this.hillsRate = hillsRate;
		if(this.hillsRate<0)this.hillsRate=0;
	}
	@Override
	public String toString() {
		return "[Name:"+name+"; HillsHeight: "+hillsHeight+"; HillsRate:"+hillsRate+"; Radius:"+radius+"]";
	}

}
