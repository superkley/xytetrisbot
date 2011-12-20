package cn.keke.qqtetris;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class TransformationResult {
	public TransformationResult(BlockType block, int rotationIdx, int targetX,
			int targetY, int score, ArrayList<Point> cleverPoints) {
		super();
		this.score = score;
		// this.hash = hash;
		// this.highest = highest;
		// this.lowest = lowest;
		// this.occupied = occupied;
		// this.holes = holes;
		// this.edges = edges;
		// this.walls = walls;
		// this.floors = floors;
		// this.blockades = blockades;
		this.x = targetX;
		this.y = targetY;
		this.rotationIdx = rotationIdx;
		this.block = block;
		this.cleverPoints = cleverPoints;
	}

	public TransformationResult(BlockType block) {
		this.block = block;
		this.x = -1;
		this.y = -1;
		this.rotationIdx = -1;
		this.score = Integer.MIN_VALUE;
	}

	public TransformationResult(TransformationResult r) {
		this.block = r.block;
		this.x = r.x;
		this.y = r.y;
		this.rotationIdx = r.rotationIdx;
		this.score = r.score;
		this.cleverPoints = r.getCleverPoints();
	}

	public TransformationResult(TransformationResult r,
			double score) {
		this(r);
		this.score = score;
	}

	public void update(int rIdx, int x, int y, double s,
			ArrayList<Point> cleverPoints) {
		this.score = s;
		this.rotationIdx = rIdx;
		this.x = x;
		this.y = y;
		this.cleverPoints = cleverPoints;
	}

	public final BlockType block;
	private int rotationIdx;
	private int x;
	private int y;
	private double score;
	ArrayList<Point> cleverPoints;

	// public final int hash;
	// public final int highest;
	// public final int lowest;
	// public final int occupied;
	// public final int holes;
	// public final int edges;
	// public final int walls;
	// public final int floors;
	// public final int blockades;
	/**
	 * @return the block
	 */
	public BlockType getBlock() {
		return this.block;
	}

	/**
	 * @return the rotationIdx
	 */
	public int getRotationIdx() {
		return this.rotationIdx;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return this.score;
	}

	@Override
	public String toString() {
		return "TR [rIdx=" + this.rotationIdx + ", x=" + this.x + ", y="
				+ this.y + ", score=" + this.score + ", clever="
				+ this.cleverPoints + "]";
	}

	public ArrayList<Point> getCleverPoints() {
		return this.cleverPoints;
	}

}
