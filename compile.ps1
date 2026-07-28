$files = Get-Content sources.txt
$args = @("-encoding", "UTF-8", "-cp", "lib/*;src") + $files
& javac $args
