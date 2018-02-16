<?php
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if(isset($_POST['sub']) && isset($_POST['pass']) && $_POST['idu']) :
if($_POST['pass']==$_POST['cpass']) :
      $pass = md5($_POST['pass']);
      $idu = $_POST['idu'];

$query = mysqli_query($db,"update users set password = '$pass'  where idu = '$idu'");
if($query) :
	echo "Password Changed Successfully";
else :
	echo "Request Fail";
	endif;
else :
echo "<p style='color:#ff8080'>Confirm Password Not Match</p>";

endif;
endif;
?>