package org.exxus.mickro


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

fun main() {
    val inputFile = File("tile_sheet.png")
    val tileSize = 16
    val outputFile = File("tileset.jpg")
    val similarityThreshold = 0.05 // до 5% отличий считаем одинаковыми

    val image = ImageIO.read(inputFile)
    val tilesX = image.width / tileSize
    val tilesY = image.height / tileSize

    val uniqueTiles = mutableListOf<BufferedImage>()

    for (y in 0 until tilesY) {
        for (x in 0 until tilesX) {
            val tile = image.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize)

            val isDuplicate = uniqueTiles.any { existing ->
                isSimilar(tile, existing, similarityThreshold)
            }

            if (!isDuplicate) {
                uniqueTiles.add(tile)
            }
        }
    }

    println("🔍 Уникальных с учётом похожести: ${uniqueTiles.size}")

    val gridSize = ceil(sqrt(uniqueTiles.size.toDouble())).toInt()
    val rows = ceil(uniqueTiles.size / gridSize.toDouble()).toInt()
    val tileset = BufferedImage(gridSize * tileSize, rows * tileSize, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = tileset.createGraphics()

    uniqueTiles.forEachIndexed { index, tile ->
        val x = (index % gridSize) * tileSize
        val y = (index / gridSize) * tileSize
        g.drawImage(tile, x, y, null)
    }

    g.dispose()
    ImageIO.write(tileset, "jpg", outputFile)
    println("✅ Tileset сохранён: ${outputFile.absolutePath}")
}

// Сравнивает два изображения с допустимым процентом различий
fun isSimilar(img1: BufferedImage, img2: BufferedImage, threshold: Double): Boolean {
    var diffPixels = 0
    val totalPixels = img1.width * img1.height

    for (y in 0 until img1.height) {
        for (x in 0 until img1.width) {
            if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                diffPixels++
                if (diffPixels > totalPixels * threshold) return false
            }
        }
    }

    return true
}