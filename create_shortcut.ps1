$WshShell = New-Object -comObject WScript.Shell
$Shortcut = $WshShell.CreateShortcut("C:\Users\duchi\OneDrive\Desktop\HDK Management.lnk")
$Shortcut.TargetPath = "C:\Users\duchi\OneDrive\Desktop\HDKManagement_New\output\HDKManagement\HDKManagement.exe"
$Shortcut.WorkingDirectory = "C:\Users\duchi\OneDrive\Desktop\HDKManagement_New\output\HDKManagement"
$Shortcut.Save()
