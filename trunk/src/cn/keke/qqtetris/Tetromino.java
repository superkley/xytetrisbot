package cn.keke.qqtetris;

public class Tetromino {
    public BlockType block;
    public BlockRotation rotation;
    public int rotationIdx;
    public int x;
    public int y;
    public MoveResult move;

    public Tetromino(BlockType block, int rotationIdx, int x, int y) {
        super();
        this.block = block;
        this.rotation = block.rotations[rotationIdx];
        this.rotationIdx = rotationIdx;
        this.x = x;
        this.y = y;
    }

    public void set(final BlockType block, final int rotationIdx, final int x, final int y) {
        this.block = block;
        this.rotation = block.rotations[rotationIdx];
        this.rotationIdx = rotationIdx;
        this.x = x;
        this.y = y;
    }

    public Tetromino() {
        this.move = new MoveResult(null, null, 0, 0, false);
    }
    
    public Tetromino(MoveResult move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "Tetromino [block=" + this.block + ", rotation=" + this.rotation + ", x=" + this.x + ", y=" + this.y
                + "]";
    }

    public void reset() {
        this.block = null;
        this.move.reset();
    }

    public boolean isValid() {
        return this.block != null;
    }

    public void setMove(MoveResult result) {
        this.move = result;
    }

    public void from(Tetromino o) {
        this.block = o.block;
        this.rotation = o.rotation;
        this.rotationIdx = o.rotationIdx;
        this.x = o.x;
        this.y = o.y;
    }
    
    public static final Tetromino from(final Tetromino o, final MoveResult move) {
        final Tetromino t = new Tetromino(move);
        t.set(o.block, o.rotationIdx, o.x, o.y);
        return t;
    }

}
