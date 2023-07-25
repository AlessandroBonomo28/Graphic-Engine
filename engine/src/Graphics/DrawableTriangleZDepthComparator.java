package Graphics;

import java.util.Comparator;

public class DrawableTriangleZDepthComparator implements Comparator<DrawableTriangle>{

	@Override
	public int compare(DrawableTriangle t1, DrawableTriangle t2) {
		/*
		double avg1 = t1.getV1().getPosition().z+ 
				   t1.getV2().getPosition().z+ 
				   t1.getV3().getPosition().z;
		
		double avg2 = t1.getV1().getPosition().z+ 
					   t1.getV2().getPosition().z+ 
					   t1.getV3().getPosition().z;
		
		avg2/=3;
		avg1/=3;
		return (int) ( (avg1-avg2)*100);
		*/
		return (int) (t1.getV1().getPosition().z - t2.getV1().getPosition().z);
	}

}
