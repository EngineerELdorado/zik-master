<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if($_REQUEST):
$id = $_REQUEST['idu'];
$val = $_REQUEST['val'];
$query = $conn->query("update chat SET chat.read ='$val' where chat.to = '$id'");
if($query){
  
          $set['chat'][] = 'Updated';
          $set['success'] = 1;
}
else{
 $set['chat'][]='Not Updated';
 $set['success'] = 0;
}
endif;
$val = json_encode($set);
echo $val;
?>