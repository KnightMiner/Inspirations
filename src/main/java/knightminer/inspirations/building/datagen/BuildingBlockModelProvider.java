package knightminer.inspirations.building.datagen;

import com.google.common.collect.ImmutableList;
import knightminer.inspirations.Inspirations;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Generates the models for the Building module */
public class BuildingBlockModelProvider extends BlockModelProvider {
	public BuildingBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Inspirations.modID, existingFileHelper);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations - Building Block Models";
	}

	@Override
	protected void registerModels() {
		makeMulch("plain");
		makeMulch("brown");
		makeMulch("red");
		makeMulch("black");
		makeMulch("blue");

		makeFlower("cyan");
		makeFlower("paeonia");
		makeFlower("rose");
		makeFlower("syringa");

		makeGlassTrapdoor("bottom", "top", "open");

		BlockModelBuilder bush = getBuilder("block/enlightened_bush/template")
				.parent(getExistingFile(mcLoc("block/block")))
				.texture("particle", "#leaves");
		// The lights have alpha, so they override some pixels and leave others untouched.
		bush.element().allFaces((dir, face) -> face.texture("#leaves").cullface(dir).tintindex(0));
		bush.element().allFaces((dir, face) -> face.texture("#lights").cullface(dir));

		makeEnlightenedBush(bush, "white");
		makeEnlightenedBush(bush,"green");
		makeEnlightenedBush(bush,"red");
		makeEnlightenedBush(bush,"blue");


		BlockModelBuilder bookshelf_base = getBuilder("block/bookshelf/base")
			.texture("particle", "#texture")
			.transforms()
				.transform(ModelBuilder.Perspective.GUI)
					.rotation(30, 45, 0)
					.translation(2, -1, 0)
					.scale(0.625f)
				.end()
				.transform(ModelBuilder.Perspective.FIXED)
					.rotation(0, 180, 0)
					.translation(0, 0, -2)
					.scale(0.5f)
				.end()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
					.rotation(0, 315, 0)
					.translation(0, 0, 0.25f)
					.scale(0.4f)
				.end()
				.transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
					.rotation(75, 225, 0)
					.translation(0, 2.5f, 0.25f)
					.scale(0.375f)
				.end()
			.end();

		// The heights of each book in order.
		int[] normHeights = new int[] {
				5, 4, 5, 6, 4, 5, 4,
				4, 5, 6, 4, 5, 5, 6
		};

		makeBookshelf(
				bookshelf_base, "normal",
				true,
				normHeights,
				// Which books have labels.
				new boolean[] {
					true, false, false, true, false, false, true,
					false, false, false, false, true,  false, false
				},
				modLoc("block/books"),
				modLoc("block/books_overlay")
		);
		makeBookshelf(bookshelf_base, "rainbow",
				false,
				normHeights, new boolean[] { false },
				modLoc("block/books_rainbow"),
				modLoc("block/books_rainbow")
		);
		makeBookshelf(
				bookshelf_base,
				"ancient",
				false,
				new int[] {
						6, 5, 6, 4, 6, 5, 6,
						6, 5, 6, 4, 5, 6, 4
				}, new boolean[] { false },
				modLoc("block/books_ancient"),
				null
		);
		makeBookshelf(bookshelf_base, "tomes",
				false,
				new int[] { 5 },
				new boolean[] { false },
				modLoc("block/books_tomes"),
				null
		);
	}

	private void makeMulch(String color) {
		getBuilder("block/mulch/" + color)
				.parent(getExistingFile(mcLoc("block/cube_all")))
				.texture("all", modLoc("block/mulch_" + color));
	}

	private void makeFlower(String type) {
		ResourceLocation tex = modLoc("block/flower_" + type);
		getBuilder("block/flower/" + type)
				.parent(getExistingFile(mcLoc("block/cross")))
				.texture("cross", tex);
		getBuilder("block/potted/" + type)
				.parent(getExistingFile(mcLoc("block/flower_pot_cross")))
				.texture("plant", tex);
	}

	private void makeGlassTrapdoor(String... variants) {
		for(String variant: variants) {
			getBuilder("block/trapdoor/glass_trapdoor_" + variant)
					.parent(getExistingFile(mcLoc("block/template_orientable_trapdoor_" + variant)))
					.texture("texture", modLoc("block/glass_trapdoor"));
		}
	}

	private void makeEnlightenedBush(ModelFile template, String color) {
		getBuilder("block/enlightened_bush/" + color)
				.parent(template)
				.texture("leaves", mcLoc("block/oak_leaves"))
				.texture("lights", modLoc("block/lights_" + color));
	}


	/** Construct the three bookshelf models for each variant.
	 * @param base The base model to inherit from
	 * @param variant The name of the variant to create.
	 * @param tint If true, tint the books.
	 * @param heights An array of pixel heights for each book in order.
	 * @param hasLabel If each book has an extra label element or not. If labelTex is null this is ignored.
	 * @param bookTex The texture for the books.
	 * @param labelTex The texture for the labels, or null if none are used.
	 */
	private void makeBookshelf(
			ModelFile base,
			String variant,
			boolean tint,
			int[] heights,
			boolean[] hasLabel,
			ResourceLocation bookTex,
			@Nullable ResourceLocation labelTex
			) {
		BlockModelBuilder shelfBack = getBuilder(String.format("block/bookshelf/%s_back", variant)).parent(base);
		BlockModelBuilder shelfFront = getBuilder(String.format("block/bookshelf/%s_front", variant));
		BlockModelBuilder shelfBoth = getBuilder(String.format("block/bookshelf/%s_both", variant));

		// Common to all three shelves.
		for(BlockModelBuilder shelf: ImmutableList.of(shelfBack, shelfFront, shelfBoth)) {
			shelf.texture("texture", modLoc("block/mixed_planks"));
		}
		if (labelTex == null) {
			// Force no labels.
			hasLabel = new boolean[] { false };
		}

		// Then add the correct planks to each.
		addSingleShelf(shelfBack, false);
		addSingleShelf(shelfFront, true);
		addDoubleShelf(shelfBoth);

		// Finally copy in all the books.
		for(int i = 0; i < 28; i++) {
			int height = heights[i % heights.length];
			boolean label = hasLabel[i % hasLabel.length];
			int tintInd = tint ? i + 1 : -1;
			if (i < 14) {
				addBook(shelfBack, null, i, 0, height)
						.faces((dir, face) -> face.tintindex(tintInd))
						.texture("#book" + i);
				addBook(shelfFront, Direction.SOUTH, i, 8, height)
						.faces((dir, face) -> face.tintindex(tintInd))
						.texture("#book" + i);
				addBook(shelfBoth, Direction.SOUTH, i,8, height)
						.faces((dir, face) -> face.tintindex(tintInd))
						.texture("#book" + i);

				shelfBack.texture("book" + i, bookTex);
				shelfFront.texture("book" + i, bookTex);
				shelfBoth.texture("book" + i, bookTex);

				if (label) {
					addBook(shelfBack, null, i, 0, height)
							.texture("#bookLabel" + i);
					addBook(shelfFront, Direction.SOUTH, i, 8, height)
							.texture("#bookLabel" + i);
					addBook(shelfBoth, Direction.SOUTH, i, 8, height)
							.texture("#bookLabel" + i);

					shelfBack.texture("bookLabel" + i, labelTex);
					shelfFront.texture("bookLabel" + i, labelTex);
					shelfBoth.texture("bookLabel" + i, labelTex);
				}
			} else {
				addBackBook(shelfBoth, i - 8, height)
						.faces((dir, face) -> face.tintindex(tintInd))
						.texture("#book" + i);
				shelfBoth.texture("book" + i, bookTex);
				if (label) {
					addBackBook(shelfBoth, i - 8, height)
							.texture("#bookLabel" + i);
					shelfBoth.texture("bookLabel" + i, labelTex);
				}
			}
		}
	}

	/** Construct the elements for a single-side shelf. */
	private void addSingleShelf(BlockModelBuilder builder, boolean isFront) {
		// If on the front, the interior can be culled.
		// Otherwise it always renders.
		Direction interior = isFront ? Direction.SOUTH: null;
		Direction back = isFront ? null : Direction.NORTH;
		float offset = isFront ? 8 : 0;

		Direction[] sides = new Direction[] {
				Direction.UP, Direction.DOWN,
				Direction.EAST, Direction.WEST
		};

		// Exterior.
		ModelBuilder.ElementBuilder exterior = builder.element()
				.from(0, 0, offset)
				.to(16, 16, offset + 8)
				.face(Direction.NORTH).cullface(back).texture("#texture").end();

		// Add each of the 4 sides
		for (Direction side: sides) {
			exterior.face(side).cullface(side).texture("#texture");
			float x1 = side.getXOffset() == -1 ? 15 : 0;
			float x2 = side.getXOffset() == 1 ? 1 : 16;
			float y1 = side.getYOffset() == -1 ? 15 : 0;
			float y2 = side.getYOffset() == 1 ? 1 : 16;
			builder.element()
					.from(x1, y1, offset + 1)
					.to(x2, y2, offset + 8)
					.face(Direction.SOUTH).cullface(interior).end()
					.face(side).cullface(interior).end()
					.texture("#texture")
			.end();
		}

		// Then the interior backing.
		builder.element()
				.from(1, 1, offset + 1)
				.to(15, 15, offset + 1)
				.face(Direction.SOUTH).cullface(interior).end()
				.texture("#texture");

		// Finally the middle shelf.
		builder.element()
				.from(1, 7, offset + 1)
				.to(15, 9, offset + 8)
				.face(Direction.UP).cullface(interior).end()
				.face(Direction.DOWN).cullface(interior).end()
				.face(Direction.SOUTH).cullface(interior).end()
				.texture("#texture");
	}

	/** Construct the planks for a double-shelf. */
	private void addDoubleShelf(BlockModelBuilder builder) {
		Direction[] sides = new Direction[] {
				Direction.UP, Direction.DOWN,
				Direction.EAST, Direction.WEST
		};

		// Exterior.
		ModelBuilder.ElementBuilder exterior = builder.element();

		// Add each of the 4 side sets.
		for (Direction side: sides) {
			exterior.face(side).cullface(side).texture("#texture");
			float x1 = side.getXOffset() == -1 ? 15 : 0;
			float x2 = side.getXOffset() == 1 ? 1 : 16;
			float y1 = side.getYOffset() == -1 ? 15 : 0;
			float y2 = side.getYOffset() == 1 ? 1 : 16;
			builder.element()
					.from(x1, y1, 0)
					.to(x2, y2, 7)
					.face(Direction.NORTH).cullface(Direction.NORTH).end()
					.face(side).cullface(Direction.NORTH).end()
					.texture("#texture")
			.end();
			builder.element()
					.from(x1, y1, 9)
					.to(x2, y2, 16)
					.face(Direction.SOUTH).cullface(Direction.SOUTH).end()
					.face(side).cullface(Direction.SOUTH).end()
					.texture("#texture")
			.end();
		}

		// Then the interior backing.
		builder.element()
				.from(1, 1, 7)
				.to(15, 15, 9)
				.face(Direction.SOUTH).cullface(Direction.SOUTH).end()
				.face(Direction.NORTH).cullface(Direction.NORTH).end()
				.texture("#texture");

		// Finally the middle shelves.
		builder.element()
				.from(1, 7, 0)
				.to(15, 9, 7)
				.face(Direction.UP).cullface(Direction.NORTH).end()
				.face(Direction.DOWN).cullface(Direction.NORTH).end()
				.face(Direction.NORTH).cullface(Direction.NORTH).end()
				.texture("#texture");
		builder.element()
				.from(1, 7, 9)
				.to(15, 9, 16)
				.face(Direction.UP).cullface(Direction.SOUTH).end()
				.face(Direction.DOWN).cullface(Direction.SOUTH).end()
				.face(Direction.SOUTH).cullface(Direction.SOUTH).end()
				.texture("#texture");
	}

	/** Generate a book quad for a shelf. **/
	private BlockModelBuilder.ElementBuilder addBook(BlockModelBuilder builder, Direction cullDir, int pos, int offset, int height) {
		// X/Y of the book's lower-left.
		int x = 2 * (pos % 7) + 1;
		int y = pos > 7 ? 1 : 9;
		BlockModelBuilder.ElementBuilder book = builder.element()
				.from(x, y,offset + 1)
				.to(x + 2, y + height,offset + 6);

		book.face(Direction.SOUTH).cullface(cullDir);
		// Add the sides, but only if we're not flush against the side.
		if (x != 1) {
			book.face(Direction.WEST).cullface(cullDir).uvs(x+1, y, x+2, y + height);
		}
		if (x != 13) {
			book.face(Direction.EAST).cullface(cullDir).uvs(x, y, x+1, y + height);
		}
		if (height != 6) {
			book.face(Direction.UP).cullface(cullDir).uvs(x, y, x+2, y + 1);
		}
		return book;
	}

	/** Generate a book quad, on the opposite side for dual shelves. **/
	private BlockModelBuilder.ElementBuilder addBackBook(BlockModelBuilder builder, int pos, int height) {
		// X/Y of the book's lower-left.
		int x = 2 * (6 - (pos % 7)) + 1;
		int y = pos > 7 ? 1 : 9;
		BlockModelBuilder.ElementBuilder book = builder.element()
				.from(x, y,2)
				.to(x + 2, y + height,7);

		book.face(Direction.NORTH).cullface(Direction.NORTH).uvs(x, y, x+1, y+height);
		if (x != 1) {
			book.face(Direction.WEST).cullface(Direction.NORTH).uvs(x+1, y, x+2, y + height);
		}
		if (x != 13) {
			book.face(Direction.EAST).cullface(Direction.NORTH).uvs(x, y, x+1, y + height);
		}
		if (height != 6) {
			book.face(Direction.UP).cullface(Direction.NORTH).uvs(x, y, x+2, y + 1);
		}

		return book;
	}
}
