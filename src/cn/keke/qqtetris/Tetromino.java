package cn.keke.qqtetris;


public class Tetromino {
    public final BlockType     block;
    public final BlockRotation rotation;
    public final int           rotationIdx;
    public final int           x;
    public final int           y;

    public Tetromino(BlockType block, int rotationIdx, int x, int y) {
        super();
        this.block = block;
        this.rotation = block.rotations[rotationIdx];
        this.rotationIdx = rotationIdx;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Tetromino [block=" + this.block + ", rotation=" + this.rotation + ", x=" + this.x + ", y=" + this.y + "]";
    }

}
