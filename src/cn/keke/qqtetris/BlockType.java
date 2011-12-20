package cn.keke.qqtetris;

import java.awt.Point;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

public enum BlockType {
    L(0xFF9BBB49, 0xFF394919, new Point(42, 35), new Point[] { new Point(24, 17), new Point(43, 52) }, new BlockRotation[] {
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    1, 0, 0, 0,
                    1, 0, 0, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                    1, 1, 0, 0,
                    0, 1, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                    0, 0, 1, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 0, BoardUtils.intsToBooleans(
                    0, 1, 0, 0,
                    0, 1, 0, 0,
                    0, 1, 1, 0,
                    0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 1, 1, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                    1, 1, 1, 0,
                    1, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0)) }),
    I(0xFFD44949, 0xFF491919, new Point(32, 25), new Point[] { new Point(16, 25) }, new BlockRotation[] {
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    1, 1, 1, 1,
                    1, 1, 1, 1,
                    0, 0, 0, 0), 4, BoardUtils.intsToBooleans(
                            1, 1, 1, 1,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 1, 0, 1,
                    0, 1, 0, 1,
                    0, 1, 0, 1,
                    0, 1, 0, 1), 8, BoardUtils.intsToBooleans(
                            0, 0, 1, 0,
                            0, 0, 1, 0,
                            0, 0, 1, 0,
                            0, 0, 1, 0)) }),
    O(0xFFEDCC51, 0xFF594921, new Point(32, 25), new Point[] { new Point(32, 44) }, new BlockRotation[] {
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 0, BoardUtils.intsToBooleans(
                    0, 1, 1, 0,
                    0, 1, 1, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0)) }),
    T(0xFFBB51D4, 0xFF412151, new Point(42, 35), new Point[] { new Point(24, 33), new Point(43, 52) }, new BlockRotation[] {
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    1, 0, 1, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                    1, 1, 1, 0,
                    0, 1, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 1, 0,
                    1, 0, 0, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                            0, 1, 0, 0,
                            1, 1, 0, 0,
                            0, 1, 0, 0,
                            0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0), 1, BoardUtils.intsToBooleans(
                    0, 1, 0, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    1, 0, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                    0, 1, 0, 0,
                    0, 1, 1, 0,
                    0, 1, 0, 0,
                    0, 0, 0, 0)) }),
    J(0xFF10BBDC, 0xFF084151, new Point(42, 35), new Point[] { new Point(59, 17), new Point(43, 52) }, new BlockRotation[] {
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                            0, 1, 1, 0,
                            0, 1, 0, 0,
                            0, 1, 0, 0,
                            0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    1, 1, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                            1, 1, 1, 0,
                            0, 0, 1, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 0, BoardUtils.intsToBooleans(
                    0, 1, 0, 0,
                    0, 1, 0, 0,
                    1, 1, 0, 0,
                    0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0), 2, BoardUtils.intsToBooleans(
                    1, 0, 0, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0)) }),
    Z(0xFF497AED, 0xFF192959, new Point(42, 35), new Point[] { new Point(24, 17), new Point(59, 36) }, new BlockRotation[] {
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    1, 0, 0, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0), 3, BoardUtils.intsToBooleans(
                    1, 1, 0, 0,
                    0, 1, 1, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 0, BoardUtils.intsToBooleans(
                    0, 0, 1, 0,
                    0, 1, 1, 0,
                    0, 1, 0, 0,
                    0, 0, 0, 0))
             }),
    S(0xFF41B441, 0xFF194119, new Point(42, 35), new Point[] { new Point(59, 17), new Point(24, 33) }, new BlockRotation[] {
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 1, 0,
                    1, 1, 1, 0,
                    0, 0, 0, 0), 3, BoardUtils.intsToBooleans(
                            0, 1, 1, 0,
                            1, 1, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0)),
            new BlockRotation(BoardUtils.intsToBooleans(
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0), 0, BoardUtils.intsToBooleans(
                    0, 1, 0, 0,
                    0, 1, 1, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 0)) });

    // fill: 0xFF000000
    // -1: 0xFF2B7BB0
    // +2: 0xFFDB5700
    // arrow up: 0xFF2B7BB0
    // arrow down: 0xFFDB008B
    public static final int[]       BLOCK_BOTTOM_COLORS = { 0xFF2B7BB0, 0xFFDB5700, 0xFF2B7BB0, 0xFFDB008B, BlockType.I.bottomColor, BlockType.J.bottomColor,
                                                        BlockType.L.bottomColor,
                                                        BlockType.O.bottomColor,
                                                        BlockType.S.bottomColor, BlockType.T.bottomColor, BlockType.Z.bottomColor }; // arrow down
    public static final int[]       BLOCK_ROTATION_HASHCODES;
    public static final int[]       BLOCK_HASHCODE_ROTATION_REFS;
    public static final BlockType[] BLOCK_HASHCODE_TYPE_REFS;

    static {
        Arrays.sort(BLOCK_BOTTOM_COLORS);
        TreeMap<Integer, BlockType> rotationBlockTypeMap = new TreeMap<Integer, BlockType>();
        for (BlockType t : values()) {
            for (BlockRotation r : t.rotations) {
                rotationBlockTypeMap.put(Integer.valueOf(r.hash), t);
            }
        }
        int i = 0, h;
        BLOCK_ROTATION_HASHCODES = new int[rotationBlockTypeMap.size()];
        BLOCK_HASHCODE_TYPE_REFS = new BlockType[rotationBlockTypeMap.size()];
        BLOCK_HASHCODE_ROTATION_REFS = new int[rotationBlockTypeMap.size()];
        BlockType type;
        Set<Integer> keySet = rotationBlockTypeMap.keySet();
		for (Integer hash : keySet) {
            h = hash.intValue();
            BLOCK_ROTATION_HASHCODES[i] = h;
            type = rotationBlockTypeMap.get(hash);
            BLOCK_HASHCODE_TYPE_REFS[i] = type;
            for (int k = 0; k < type.rotations.length; k++) {
                BlockRotation r = type.rotations[k];
                if (h == r.hash) {
                    BLOCK_HASHCODE_ROTATION_REFS[i] = k;
                }
            }
            i++;
        }
    }
    public final int                innerColor;
    public final int                bottomColor;
    public final Point              innerCoord;
    public final Point[]            idCoords;
    public final BlockRotation[]    rotations;

    BlockType(int innerColor, int bottomColor, Point innerCoord, Point[] idCoords, BlockRotation[] rotations) {
        this.innerColor = innerColor;
        this.bottomColor = bottomColor;
        this.innerCoord = innerCoord;
        this.idCoords = idCoords;
        this.rotations = rotations;
    }

}
