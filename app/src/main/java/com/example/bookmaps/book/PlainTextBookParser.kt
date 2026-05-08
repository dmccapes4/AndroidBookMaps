package com.example.bookmaps.book

import com.example.bookmaps.database.BookPage

/**
 * Splits OCR/plain text into sequential [BookPage] rows using chapter headings and soft
 * character limits—more stable than relying on flaky printed page markers in Librivox exports.
 */
object PlainTextBookParser {
    private val chapterHeading = Regex("""(?m)^CHAPTER\s+.+$""")
    private const val ApproxCharsPerPage = 2400

    fun parse(fullText: String): List<BookPage> {
        val normalized =
            fullText
                .replace('\r', '\n')
                .replace(Regex("""[\t\f]+"""), " ")
                .replace(Regex(""" *\n *\n *\n+"""), "\n\n")
                .trim()

        val segments = splitByChapterHeadings(normalized)
        val pages = ArrayList<BookPage>(segments.size * 12)
        var pageNumber = 1
        segments.forEach { (chapterIndex, body) ->
            chunkBody(body).forEach { chunk ->
                pages += BookPage(
                    pageNumber = pageNumber++,
                    chapterNumber = chapterIndex,
                    text = chunk,
                )
            }
        }
        return pages
    }

    private fun splitByChapterHeadings(text: String): List<Pair<Int, String>> {
        val ranges = chapterHeading.findAll(text).map { it.range }.toList()
        val out = ArrayList<Pair<Int, String>>()
        if (ranges.isEmpty()) {
            out += 0 to text
            return out
        }
        val firstStart = ranges.first().first
        if (firstStart > 0) {
            val intro = text.substring(0, firstStart).trim()
            if (intro.isNotEmpty()) {
                out += 0 to intro
            }
        }
        ranges.forEachIndexed { index, range ->
            val end = ranges.getOrNull(index + 1)?.first ?: text.length
            val slice = text.substring(range.first, end).trim()
            if (slice.isNotEmpty()) {
                out += (index + 1) to slice
            }
        }
        return out
    }

    private fun chunkBody(body: String): List<String> {
        val paragraphs =
            body
                .split(Regex("\\n\\n+"))
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        val chunks = ArrayList<String>()
        val buffer = StringBuilder()
        fun flush() {
            val text = buffer.toString().trim()
            if (text.isNotEmpty()) {
                chunks += text
            }
            buffer.clear()
        }

        paragraphs.forEach { paragraph ->
            if (paragraph.length > ApproxCharsPerPage * 4) {
                flush()
                hardSplitParagraph(paragraph).forEach { part ->
                    if (buffer.isNotEmpty() && buffer.length + part.length > ApproxCharsPerPage) {
                        flush()
                    }
                    if (buffer.isNotEmpty()) {
                        buffer.append("\n\n")
                    }
                    buffer.append(part)
                    if (buffer.length >= ApproxCharsPerPage) {
                        flush()
                    }
                }
            } else if (
                buffer.isNotEmpty() &&
                buffer.length + paragraph.length + 2 > ApproxCharsPerPage
            ) {
                flush()
                buffer.append(paragraph)
            } else {
                if (buffer.isNotEmpty()) {
                    buffer.append("\n\n")
                }
                buffer.append(paragraph)
            }
        }
        flush()
        return chunks
    }

    /**
     * Splits oversized paragraphs along sentence boundaries without failing if none exist.
     */
    private fun hardSplitParagraph(paragraph: String): List<String> {
        val sentences =
            Regex("(?<=[.!?])(?=\\s)").split(paragraph).map { it.trim() }.filter { it.isNotEmpty() }
        if (sentences.size <= 1) {
            val words = paragraph.split(Regex("\\s+"))
            val parts = ArrayList<String>()
            val wb = StringBuilder()
            words.forEach { w ->
                if (wb.length + w.length + 1 > ApproxCharsPerPage && wb.isNotEmpty()) {
                    parts += wb.toString().trim()
                    wb.clear()
                }
                if (wb.isNotEmpty()) {
                    wb.append(' ')
                }
                wb.append(w)
            }
            if (wb.isNotEmpty()) {
                parts += wb.toString().trim()
            }
            return parts.ifEmpty { listOf(paragraph) }
        }
        return sentences
    }
}
