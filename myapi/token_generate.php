<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if(isset($_REQUEST['idu'])) :
$id = $_REQUEST['idu'];
$token = $_REQUEST['token_id'];
$type = $_REQUEST['device_type'];

 $query = $conn->query("UPDATE users set  users.device_token = '$token', users.device_type = '$type' where users.idu = '$id'");
 if($query):
       $inf['token update'][] = 'done';
       $inf['success']=1;
       else :
       $inf['token update']='fail';
       $inf['success']=0;
      endif;
$val = json_encode($inf);
       echo $val; 

endif;
?>