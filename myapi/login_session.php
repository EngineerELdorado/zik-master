<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);

if(isset($_REQUEST)) :
	$id = $_REQUEST['idu'];
	$time = time();
	$session = '0';
	$query = $conn->query("UPDATE users set online = '$time', logout_time = '$session' where idu = '$id'");
	  if($query) :
       $inf['login_session'][] = 'done';
       $inf['success']=1;
       else:
	   $inf['login_session'][] = 'fail';
       $inf['success']=0;
	   endif;
$val = json_encode($inf);
       echo $val; 

endif;
?>