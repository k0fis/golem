from PIL import Image

# Load your image
img = Image.open("cat-outline.png").convert("RGBA")
width, height = img.size

# Create a new image with fully transparent inside
new_img = Image.new("RGBA", (width, height), (0, 0, 0, 0))
pixels = img.load()
new_pixels = new_img.load()

# Copy only the outline (non-transparent pixels near edges)
for x in range(width):
    for y in range(height):
        r, g, b, a = pixels[x, y]
        # If pixel is not fully transparent, copy it to the new image
        if a > 0:
            new_pixels[x, y] = (r, g, b, a)

# Save the cleaned image
new_img.save("cat_outline-2.png")
