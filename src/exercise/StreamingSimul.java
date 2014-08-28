package exercise;

import java.util.Map;

import model_topoMan.Network;

public class StreamingSimul {

	Network network;
	Integer chunk_number;
	Float chunksize;
	Map<Double,Float> uploadClasses2percentage;
	
	public StreamingSimul(Network network, Integer chunk_number,
			Float chunksize, Map<Double, Float> uploadClasses2percentage) {
		super();
		this.network = network;
		this.chunk_number = chunk_number;
		this.chunksize = chunksize;
		this.uploadClasses2percentage = uploadClasses2percentage;
	}
	
	
	
	

}
