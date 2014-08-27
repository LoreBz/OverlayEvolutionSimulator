package model_streamSim;

public class Chunk implements Comparable<Chunk> {

	Integer chunk_Seq_number;
	Float chunk_size;
	Integer generation_time;

	public Chunk(Integer chunk_Seq_number, Float chunk_size,
			Integer generation_time) {
		super();
		this.chunk_Seq_number = chunk_Seq_number;
		this.chunk_size = chunk_size;
		this.generation_time = generation_time;
	}

	public Integer getChunk_Seq_number() {
		return chunk_Seq_number;
	}

	public void setChunk_Seq_number(Integer chunk_Seq_number) {
		this.chunk_Seq_number = chunk_Seq_number;
	}

	public Float getChunk_size() {
		return chunk_size;
	}

	public void setChunk_size(Float chunk_size) {
		this.chunk_size = chunk_size;
	}

	public Integer getGeneration_time() {
		return generation_time;
	}

	public void setGeneration_time(Integer generation_time) {
		this.generation_time = generation_time;
	}

	@Override
	public int compareTo(Chunk other) {
		// compareTo should return < 0 if this is supposed to be
		// less than other, > 0 if this is supposed to be greater than
		// other and 0 if they are supposed to be equal
		if (this.generation_time < other.generation_time)
			return -1;
		else if (this.generation_time > other.generation_time)
			return 1;
		else
			return 0;
	}

}
