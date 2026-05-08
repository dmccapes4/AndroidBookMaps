#!/usr/bin/env python3
"""
Prepare a plain-text book for the Android app (mirrors a future "fetch from server" step).

By default copies the bundled Librivox Aristopia export into app build-time assets.
Optionally downloads from an HTTP(S) URL into the same destination so CI or a
release pipeline can pull the canonical edition from your server.
"""

from __future__ import annotations

import argparse
import shutil
import sys
import urllib.request
from pathlib import Path


def default_paths(repo_root: Path) -> tuple[Path, Path]:
    src = repo_root / "aristopia_rg_librivox" / "aristopia-full-text.txt"
    dest = (
        repo_root
        / "app"
        / "build"
        / "generated-book-assets"
        / "books"
        / "aristopia-full-text.txt"
    )
    return src, dest


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--repo-root",
        type=Path,
        default=Path(__file__).resolve().parents[1],
        help="BookMaps repository root (parent of app/, aristopia_rg_librivox/).",
    )
    parser.add_argument(
        "--url",
        help="If set, download this URL to the output path instead of copying the local file.",
    )
    parser.add_argument(
        "--out",
        type=Path,
        help="Output file path (defaults to Gradle syncBookAssets destination).",
    )
    args = parser.parse_args()

    repo_root: Path = args.repo_root
    default_src, default_dest = default_paths(repo_root)
    dest: Path = args.out or default_dest

    dest.parent.mkdir(parents=True, exist_ok=True)

    if args.url:
        print(f"Downloading book from {args.url!r} -> {dest}", file=sys.stderr)
        urllib.request.urlretrieve(args.url, dest)
    else:
        if not default_src.is_file():
            print(f"Missing source book: {default_src}", file=sys.stderr)
            return 1
        print(f"Copying {default_src} -> {dest}", file=sys.stderr)
        shutil.copy2(default_src, dest)

    print(dest)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
