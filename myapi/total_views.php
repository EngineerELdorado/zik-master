<?php 
require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
	mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['userid']) && $_REQUEST['trackid']):
	$value = 1;
	//$uid = $_REQUEST['uid'];
	$usid = $_REQUEST['userid'];
	$trackid = $_REQUEST['trackid'];
	$query = $conn->query("insert into views(views.by,views.track) values('$usid','$trackid')");
	$q = $conn->query("select views from tracks where id = '$trackid'");
	$n = $q->num_rows;
	if($n > 0) :
		$t = $q->fetch_object();
		//print_r($t);
		$total =  $value + $t->views;
	endif;
		$qry = $conn->query("update tracks set tracks.views = '$total' where tracks.id = '$trackid'");
	if($qry) :
		$set['view'][] = 'views done';
		$set['success'] =1;
 	else : 
		$set['view'][] = 'views fail';
		$set['success'] =0;
	endif;
		$val = json_encode($set);  
		echo $val; 
 endif;
 
 ?>