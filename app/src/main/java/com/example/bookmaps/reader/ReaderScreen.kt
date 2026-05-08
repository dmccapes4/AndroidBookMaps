@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class,
)

package com.example.bookmaps.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookmaps.database.Bookmark
import com.example.bookmaps.database.ChapterStart
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private sealed interface BookmarkOverlay {
    data object Hidden : BookmarkOverlay

    data object AddNew : BookmarkOverlay

    data class Viewing(
        val bookmark: Bookmark,
    ) : BookmarkOverlay

    data class Editing(
        val bookmark: Bookmark,
    ) : BookmarkOverlay
}

@Composable
fun ReaderScreen(
    vm: ReaderViewModel = viewModel(),
) {
    val bootstrapping by vm.bootstrapping.collectAsStateWithLifecycle()
    val maxPage by vm.maxPage.collectAsStateWithLifecycle()
    val readerPage by vm.readerPage.collectAsStateWithLifecycle()
    val chapters by vm.chapterStarts.collectAsStateWithLifecycle()
    val bookmarks by vm.allBookmarks.collectAsStateWithLifecycle()

    val density = LocalDensity.current
    val swipeThresholdPx = remember(density) { with(density) { 72.dp.toPx() } }

    var chapterPickerOpen by remember { mutableStateOf(false) }
    var bookmarkSheetOpen by remember { mutableStateOf(false) }
    val bookmarkSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
    var bookmarkOverlay by remember { mutableStateOf<BookmarkOverlay>(BookmarkOverlay.Hidden) }
    var pendingDeleteBookmark by remember { mutableStateOf<Bookmark?>(null) }

    val scope = rememberCoroutineScope()

    val pageCount = maxOf(maxPage, 1)
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { pageCount },
        )

    LaunchedEffect(readerPage, maxPage, bootstrapping) {
        if (!bootstrapping && maxPage > 0) {
            val idx = (readerPage - 1).coerceIn(0, maxPage - 1)
            if (pagerState.settledPage != idx) {
                pagerState.scrollToPage(idx)
            }
        }
    }

    LaunchedEffect(pagerState, maxPage, bootstrapping) {
        if (bootstrapping || maxPage <= 0) {
            return@LaunchedEffect
        }
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { settled ->
                vm.syncReaderPageFromPager(settled)
            }
    }

    val chapterLabel =
        remember(readerPage, chapters) {
            labelForChapter(chapters, readerPage)
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(chapterLabel, style = MaterialTheme.typography.titleSmall)
                        if (maxPage > 0 && !bootstrapping) {
                            Text(
                                "Page $readerPage · $maxPage",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    ),
            )
        },
    ) { padding ->
        if (bootstrapping || maxPage <= 0) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Box(Modifier.padding(padding).fillMaxSize()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 1,
                key = { it },
            ) { pageIndex ->
                PagedReadingBody(
                    modifier = Modifier.fillMaxSize(),
                    pageOneBased = pageIndex + 1,
                    vm = vm,
                )
            }

            VerticalSwipeRevealZone(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(88.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                hint = "Swipe down — chapters",
                swipeThresholdPx = swipeThresholdPx,
                opensOnFingerMovingDownward = true,
                onReveal = {
                    chapterPickerOpen = true
                    if (bookmarkSheetOpen) {
                        scope.launch {
                            bookmarkSheetState.hide()
                            bookmarkSheetOpen = false
                        }
                    }
                    bookmarkOverlay = BookmarkOverlay.Hidden
                },
            )

            VerticalSwipeRevealZone(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(92.dp),
                hint = "Swipe up — bookmarks",
                swipeThresholdPx = swipeThresholdPx,
                opensOnFingerMovingDownward = false,
                onReveal = {
                    bookmarkOverlay = BookmarkOverlay.Hidden
                    chapterPickerOpen = false
                    bookmarkSheetOpen = true
                },
            )

            if (chapterPickerOpen) {
                ChapterPickerOverlay(
                    chapters = chapters,
                    currentPage = readerPage,
                    onDismiss = { chapterPickerOpen = false },
                    onSelectStartPage = {
                        chapterPickerOpen = false
                        vm.goToPage(it)
                    },
                )
            }

            if (bookmarkSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = {
                        bookmarkSheetOpen = false
                        bookmarkOverlay = BookmarkOverlay.Hidden
                    },
                    sheetState = bookmarkSheetState,
                ) {
                    BookmarkSheetInner(
                        bookmarks = bookmarks,
                        currentPage = readerPage,
                        onAdd = { bookmarkOverlay = BookmarkOverlay.AddNew },
                        onSelect = { bookmarkOverlay = BookmarkOverlay.Viewing(it) },
                    )
                }
            }

            BookmarkOverlayDialogs(
                overlay = bookmarkOverlay,
                onDismissOverlay = {
                    bookmarkOverlay = BookmarkOverlay.Hidden
                },
                onRequestEdit = { bookmarkOverlay = BookmarkOverlay.Editing(it) },
                onRequestDelete = {
                    pendingDeleteBookmark = it
                    bookmarkOverlay = BookmarkOverlay.Hidden
                },
                onNavigateToBookmark = { page ->
                    bookmarkOverlay = BookmarkOverlay.Hidden
                    vm.goToPage(page)
                    if (bookmarkSheetOpen) {
                        scope.launch {
                            bookmarkSheetState.hide()
                            bookmarkSheetOpen = false
                        }
                    }
                },
                vm = vm,
                scope = scope,
            )

            pendingDeleteBookmark?.let { target ->
                AlertDialog(
                    onDismissRequest = { pendingDeleteBookmark = null },
                    title = { Text("Delete bookmark") },
                    text = { Text("Remove “${target.title}”?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    vm.deleteBookmark(target)
                                    pendingDeleteBookmark = null
                                    bookmarkOverlay = BookmarkOverlay.Hidden
                                }
                            },
                            colors =
                                ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error,
                                ),
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { pendingDeleteBookmark = null }) {
                            Text("Cancel")
                        }
                    },
                )
            }

            LaunchedEffect(bookmarkSheetOpen) {
                if (bookmarkSheetOpen) {
                    bookmarkSheetState.expand()
                }
            }
        }
    }
}

@Composable
private fun PagedReadingBody(
    pageOneBased: Int,
    vm: ReaderViewModel,
    modifier: Modifier = Modifier,
) {
    val pg by vm.observePage(pageOneBased).collectAsStateWithLifecycle(initialValue = null)
    ReaderPageContent(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        body = pg?.text.orEmpty(),
    )
}

@Composable
private fun ReaderPageContent(
    body: String,
    modifier: Modifier = Modifier,
) {
    if (body.isBlank()) {
        Box(modifier.then(Modifier.fillMaxSize()), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.padding(48.dp))
        }
    } else {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(text = body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun VerticalSwipeRevealZone(
    modifier: Modifier,
    hint: String,
    swipeThresholdPx: Float,
    opensOnFingerMovingDownward: Boolean,
    onReveal: () -> Unit,
) {
    var acc by remember { mutableFloatStateOf(0f) }

    Box(
        modifier.pointerInput(swipeThresholdPx, opensOnFingerMovingDownward) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dy ->
                        acc += dy
                    },
                    onDragEnd = {
                        val downward = opensOnFingerMovingDownward && acc >= swipeThresholdPx
                        val upward = !opensOnFingerMovingDownward && -acc >= swipeThresholdPx
                        if (downward || upward) {
                            onReveal()
                        }
                        acc = 0f
                    },
                    onDragCancel = {
                        acc = 0f
                    },
                )
            },
        contentAlignment = if (opensOnFingerMovingDownward) Alignment.BottomCenter else Alignment.TopCenter,
    ) {
        Text(
            text = hint,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
private fun ChapterPickerOverlay(
    chapters: List<ChapterStart>,
    currentPage: Int,
    onDismiss: () -> Unit,
    onSelectStartPage: (Int) -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
            .clickable(onClick = onDismiss),
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
        ) {
            val panelHeight = maxHeight * 0.62f
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(panelHeight)
                        .align(Alignment.TopCenter)
                        .clickable(enabled = false) {},
                tonalElevation = 3.dp,
            ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    "Chapters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(chapters, key = { it.chapterNumber }) { chapter ->
                        val selected = currentPage >= chapter.startPage
                        ListItem(
                            headlineContent = {
                                Text(labelForChapterNumber(chapter.chapterNumber))
                            },
                            supportingContent = {
                                Text(
                                    "Starts page ${chapter.startPage}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            trailingContent = {
                                if (selected) {
                                    Text(
                                        "●",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            },
                            modifier =
                                Modifier.clickable {
                                    onSelectStartPage(chapter.startPage)
                                },
                        )
                        HorizontalDivider()
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun BookmarkSheetInner(
    bookmarks: List<Bookmark>,
    currentPage: Int,
    onAdd: () -> Unit,
    onSelect: (Bookmark) -> Unit,
) {
    Column(Modifier.padding(bottom = 32.dp).fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Bookmarks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add bookmark")
            }
        }
        Text(
            text = "On page $currentPage — swipe the bottom strip upward to reopen this sheet.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(bookmarks, key = { it.id }) { bm ->
                ListItem(
                    headlineContent = { Text(bm.title) },
                    supportingContent = {
                        Text(
                            "Page ${bm.pageNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    trailingContent = {
                        Text(bm.note.take(40) + if (bm.note.length > 40) "…" else "")
                    },
                    modifier = Modifier.clickable { onSelect(bm) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun BookmarkOverlayDialogs(
    overlay: BookmarkOverlay,
    onDismissOverlay: () -> Unit,
    onRequestEdit: (Bookmark) -> Unit,
    onRequestDelete: (Bookmark) -> Unit,
    onNavigateToBookmark: (Int) -> Unit,
    vm: ReaderViewModel,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    when (val o = overlay) {
        BookmarkOverlay.AddNew -> {
            var titleDraft by remember { mutableStateOf("") }
            var noteDraft by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = onDismissOverlay,
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                vm.addBookmark(titleDraft, noteDraft)
                                onDismissOverlay()
                            }
                        },
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissOverlay) {
                        Text("Cancel")
                    }
                },
                title = { Text("New bookmark") },
                text = {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = titleDraft,
                            onValueChange = { titleDraft = it },
                            label = { Text("Title") },
                            singleLine = true,
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = noteDraft,
                            onValueChange = { noteDraft = it },
                            label = { Text("Note") },
                            minLines = 3,
                        )
                    }
                },
            )
        }

        is BookmarkOverlay.Editing -> {
            var titleDraft by remember(o.bookmark.id) { mutableStateOf(o.bookmark.title) }
            var noteDraft by remember(o.bookmark.id) { mutableStateOf(o.bookmark.note) }

            AlertDialog(
                onDismissRequest = onDismissOverlay,
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                vm.updateBookmark(o.bookmark, titleDraft, noteDraft)
                                onDismissOverlay()
                            }
                        },
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissOverlay) {
                        Text("Cancel")
                    }
                },
                title = { Text("Edit bookmark") },
                text = {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = titleDraft,
                            onValueChange = { titleDraft = it },
                            label = { Text("Title") },
                            singleLine = true,
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = noteDraft,
                            onValueChange = { noteDraft = it },
                            label = { Text("Note") },
                            minLines = 3,
                        )
                    }
                },
            )
        }

        is BookmarkOverlay.Viewing -> {
            AlertDialog(
                onDismissRequest = onDismissOverlay,
                title = { Text(o.bookmark.title) },
                text = {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = "Page ${o.bookmark.pageNumber}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier.clickable {
                                    onNavigateToBookmark(o.bookmark.pageNumber)
                                    onDismissOverlay()
                                },
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(o.bookmark.note, style = MaterialTheme.typography.bodyMedium)
                    }
                },
                confirmButton = {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onNavigateToBookmark(o.bookmark.pageNumber)
                                onDismissOverlay()
                            },
                        ) {
                            Text("Go to page ${o.bookmark.pageNumber}")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            TextButton(
                                onClick = {
                                    onRequestEdit(o.bookmark)
                                },
                            ) {
                                Text("Edit")
                            }
                            TextButton(
                                onClick = { onRequestDelete(o.bookmark) },
                                colors =
                                    ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error,
                                    ),
                            ) {
                                Text("Delete")
                            }
                        }
                        TextButton(
                            modifier = Modifier.align(Alignment.End),
                            onClick = onDismissOverlay,
                        ) {
                            Text("Close")
                        }
                    }
                },
                dismissButton = {},
            )
        }

        BookmarkOverlay.Hidden -> Unit
    }
}

private fun labelForChapter(
    chapters: List<ChapterStart>,
    readerPage: Int,
): String {
    val start =
        chapters
            .filter { it.startPage <= readerPage }
            .maxByOrNull { it.startPage }
            ?: return ""
    return when (start.chapterNumber) {
        0 -> "Introduction"
        else -> "Chapter ${start.chapterNumber}"
    }
}

private fun labelForChapterNumber(n: Int): String =
    when (n) {
        0 -> "Introduction"
        else -> "Chapter $n"
    }