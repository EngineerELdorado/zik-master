<?php 
session_start();
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['permission']) && $_REQUEST['permission']=='true' && $_REQUEST['uid']!='') :
	$uid = $_REQUEST['uid'];
	$trackid = $_REQUEST['trackid'];
	$visibility = $_REQUEST['visibility'];
	$download = $_REQUEST['download'];

	$query = $conn->query("update tracks set tracks.public ='$visibility', tracks.download = '$download' where tracks.id = '$trackid' && tracks.uid = '$uid'");
    if($query):
       $inf['changed'][] = 'Done';
       $inf['success']=1;
       else :
       $inf['changed']='Failed';
       $inf['success']=0;
      
    endif;
	$val = json_encode($inf);
	echo $val; 
endif;

?>