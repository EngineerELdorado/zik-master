<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if(isset($_REQUEST['idu'])) :
$id = $_REQUEST['idu'];
$token = $_REQUEST['token'];
$time = time();
 $query = $conn->query("UPDATE users set  logout_time = '$time' where idu = '$id'");
 $conn->query("delete from device_register where user_id = '$id' && device_token = '$token'");
 if($query):
       $inf['logout'][] = 'done';
       $inf['success']=1;
       else :
       $inf['userlogin']='fail';
       $inf['success']=0;
      endif;
$val = json_encode($inf);
       echo $val; 

endif;
?>