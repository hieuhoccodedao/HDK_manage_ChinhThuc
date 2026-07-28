$files = Get-ChildItem -Path 'src' -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
$quotedFiles = $files | ForEach-Object { "`"$_`"" }
$quotedFiles | Out-File -FilePath sources.txt -Encoding default
cmd /c 'javac -encoding UTF-8 -d build\classes -cp "lib/*;src" @sources.txt 2> error.log'
Get-Content error.log
