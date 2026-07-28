Add-Type -AssemblyName System.IO.Compression.FileSystem
$jarPath = 'C:\study duchieu\Do An\HDKManagement_New\dist\HDKManagement.jar'
$zipPath = 'C:\study duchieu\Đồ Án 1\doan1\HDKManagement_New\dist\HDKManagement.jar'
$zip = [System.IO.Compression.ZipFile]::OpenRead($zipPath)
$mf = $zip.Entries | Where-Object { $_.FullName -like '*MANIFEST.MF' }
$stream = $mf.Open()
$reader = New-Object System.IO.StreamReader($stream)
$content = $reader.ReadToEnd()
$reader.Close()
$stream.Close()
$zip.Dispose()
Write-Output "=== MANIFEST.MF Content ==="
Write-Output $content
