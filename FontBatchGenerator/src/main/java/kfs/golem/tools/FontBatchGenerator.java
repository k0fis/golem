package kfs.golem.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.tools.bmfont.BitmapFontWriter;

public class FontBatchGenerator {

    // Zadej cesty
    private static final String FONT_TTF = "MedievalSharp/MedievalSharp.ttf";
    private static final String OUTPUT_DIR = "../assets/fonts/MedievalSharp/";

    // Velikosti, které chceme generovat
    private static final int[] SIZES = {12, 16}; //{24, 36, 48, 64};

    // Všechny znaky, které chceme mít (česká diakritika + interpunkce)
    private static final String CHARACTERS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789ěščřžýáíéůúóĚŠČŘŽÝÁÍÉŮÚÓ.,:;!?()[]{}<>\"'–— ";

    public static void main(String[] args) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

        new HeadlessApplication(new com.badlogic.gdx.ApplicationAdapter() {
            @Override
            public void create() {
                try {
                    FileHandle fontFile = Gdx.files.internal(FONT_TTF);
                    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

                    for (int size : SIZES) {
                        BitmapFontWriter.FontInfo info = new BitmapFontWriter.FontInfo();
                        info.padding = new BitmapFontWriter.Padding(1, 1, 1, 1);

                        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                        parameter.size = size;
                        parameter.characters = CHARACTERS;
                        parameter.magFilter = Texture.TextureFilter.Linear;
                        parameter.minFilter = Texture.TextureFilter.Linear;
                        parameter.renderCount = 3;
                        parameter.shadowColor = new Color(0, 0, 0, 0.45f);
                        parameter.packer = new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 2, false, new PixmapPacker.SkylineStrategy());

                        //BitmapFont font = generator.generateFont(parameter);

                        // Vytvoří výstupní adresář, pokud neexistuje
                        FileHandle outputDir = Gdx.files.local(OUTPUT_DIR);
                        if (!outputDir.exists()) outputDir.mkdirs();


                        String fontName = "MedievalSharp" + size;
                        FileHandle fntFile = outputDir.child(fontName + ".fnt");

                        FreeTypeFontGenerator.FreeTypeBitmapFontData data = generator.generateData(parameter);

                        BitmapFontWriter.writeFont(data, new String[] {fontName+".png"},
                            fntFile, info, 512, 512);
                        BitmapFontWriter.writePixmaps(parameter.packer.getPages(), new FileHandle(OUTPUT_DIR), fontName);

                        //font.getRegion().getTexture().getTextureData().prepare();
                        //BitmapFontWriter.writeFont(font, fntFile);

                        //System.out.println("Hotovo: " + fontName);
                    }

                    generator.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Všechny fonty byly vygenerovány!");
                System.exit(0);
            }
        }, config);
    }
}
