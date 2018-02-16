<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if(isset($_REQUEST['from'])){
$from = $_REQUEST['from'];
$to = $_REQUEST['to'];
$query = $conn->query("delete from chat where chat.from='$from' && chat.to ='$to'");
}
 if($query){
          $set['message'][]='Message Deleted Successfully';
          $set['success'] = 1;
}
else{
 $set['message'][]='Message Not Deleted';
 $set['success']= 0;
}
$val = json_encode($set);
echo $val;
?>