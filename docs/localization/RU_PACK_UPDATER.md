# RU-pack updater metadata

The current RU-pack metadata is stored in [`RU_PACK_RELEASE.json`](RU_PACK_RELEASE.json).

An updater may fetch this JSON from the GitHub repository, compare `version` and
`sha256` with the local pack, then download the ZIP from the public Google Drive
`download_url`. Google Drive authentication is not required when the file is
shared as “Anyone with the link”.

## Contract

- The RU-pack ZIP must **not** be uploaded to GitHub.
- `download_url` must point to the public Drive copy.
- The updater must download to a temporary file first.
- It must verify `sha256` and ZIP integrity before replacing the active local pack.
- It must only remove old files matching `AeronauticsPlus-RU-Pack-*.zip`.
- Only the current local version is retained by default; older local ZIPs matching the AeronauticsPlus prefix are removed after a verified update.
- `version` changes whenever a new Drive ZIP is published.
- `sha256`, `size_bytes`, `built_files` and `ru_ru_entries` must be regenerated from
  the exact ZIP being published.

This file is metadata only; it does not grant the repository access to the user's
Google Drive account.
