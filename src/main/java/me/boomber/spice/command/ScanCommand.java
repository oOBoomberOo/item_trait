package me.boomber.spice.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ScanCommand {
    private static IntegerArgumentType integer() {
        return IntegerArgumentType.integer(1, 100);
    }
    
    private static int getInt(CommandContext<CommandSourceStack> context, String name) {
        return IntegerArgumentType.getInteger(context, name);
    }
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var node = dispatcher.getRoot();
        var root = literal("scan");

        root.then(literal("sphere")
                .then(argument("radius", integer())
                        .then(build(node, context -> {
                            var radius = getInt(context, "radius");
                            return new SphereScanner(radius);
                        }))));

        root.then(literal("circle")
                .then(argument("radius", integer())
                        .then(build(node, context -> {
                            var radius = getInt(context, "radius");
                            return new CircleScanner(radius);
                        }))));
        
        root.then(literal("cylinder")
                .then(argument("radius", integer())
                        .then(argument("height", integer())
                                .then(build(node, context -> {
                                    var radius = getInt(context, "radius");
                                    var height = getInt(context, "height");
                                    return new CylinderScanner(radius, height);
                                })))));
        
        root.then(literal("rectangle")
                .then(argument("width", integer())
                        .then(argument("height", integer())
                                .then(build(node, context -> {
                                    var width = getInt(context, "width");
                                    var height = getInt(context, "height");
                                    return new RectangleScanner(width, height);
                                })))));
        
        root.then(literal("surface")
                .then(argument("radius", integer())
                        .then(build(node, context -> {
                            var radius = getInt(context, "radius");
                            return new SurfaceScanner(radius);
                        }))));

        dispatcher.register(root);
    }

    private static List<CommandSourceStack> scan(CommandSourceStack source, Scanner scanner) {
        return scanner.scan(source)
                .map(pos -> source.withPosition(Vec3.atCenterOf(pos)))
                .collect(Collectors.toList());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> build(
            CommandNode<CommandSourceStack> node,
            Function<CommandContext<CommandSourceStack>, Scanner> getScanner
    ) {
        return literal("run")
                .fork(node, context -> {
                    var scanner = getScanner.apply(context);
                    return scan(context.getSource(), scanner);
                });
    }
}

interface Scanner {
    Stream<BlockPos> scan(CommandSourceStack source);


    default BlockPos getCenter(CommandSourceStack source) {
        var pos = source.getPosition();
        return new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
    }
}

@RequiredArgsConstructor
class SphereScanner implements Scanner {
    private final int radius;

    @Override
    public Stream<BlockPos> scan(CommandSourceStack source) {
        var center = getCenter(source);
        return BlockPos.betweenClosedStream(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))
                .filter(pos -> pos.distSqr(center) <= radius * radius);
    }
}

@RequiredArgsConstructor
class CircleScanner implements Scanner {
    private final int radius;

    @Override
    public Stream<BlockPos> scan(CommandSourceStack source) {
        var center = getCenter(source);
        return BlockPos.betweenClosedStream(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius))
                .filter(pos -> pos.distSqr(center) <= radius * radius);
    }
}

@RequiredArgsConstructor
class CylinderScanner implements Scanner {
    private final int radius;
    private final int height;

    @Override
    public Stream<BlockPos> scan(CommandSourceStack source) {
        var center = getCenter(source);
        return BlockPos.betweenClosedStream(center.offset(-radius, height, -radius), center.offset(radius, height, radius))
                .filter(pos -> pos.atY(center.getY()).distSqr(center) <= radius * radius);
    }
}

@RequiredArgsConstructor
class RectangleScanner implements Scanner {
    private final int width;
    private final int height;

    @Override
    public Stream<BlockPos> scan(CommandSourceStack source) {
        var center = getCenter(source);
        return BlockPos.betweenClosedStream(center.offset(-width / 2, height, -width / 2), center.offset(width / 2, height, width / 2));
    }
}

@RequiredArgsConstructor
class SurfaceScanner implements Scanner {
    private final int radius;

    @Override
    public Stream<BlockPos> scan(CommandSourceStack source) {
        var center = getCenter(source);
        return BlockPos.betweenClosedStream(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius))
                .filter(pos -> pos.distSqr(center) <= radius * radius)
                .map(pos -> getSurface(source, pos));
    }

    private BlockPos getSurface(CommandSourceStack source, BlockPos pos) {
        return source.getLevel().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
    }
}