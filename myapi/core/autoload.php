<?php 

      
     function __autoload($classname)
	 {
		require_once "$classname.php"; 
	 }
 
    require_once  "route.php";


?>