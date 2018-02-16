<?php 
require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if(isset($_REQUEST['userid']) && $_REQUEST['trackid']):
	$value = 1;
	//$uid = $_REQUEST['uid'];
	$usid = $_REQUEST['userid'];
	$trackid = $_REQUEST['trackid'];
	$query = $conn->query("insert into downloads(downloads.by,downloads.track) values('$usid','$trackid')");
	$q = $conn->query("select downloads from tracks where id = '$trackid'");
	$n = $q->num_rows;
	if($n > 0) :
		$t = $q->fetch_object();
		//print_r($t);
		$total =  $value + $t->downloads;
	endif;
		$qry = $conn->query("update tracks set tracks.downloads = '$total' where tracks.id = '$trackid'");
	if($qry) :
		$set['downloads'][] = 'downloads done';
		$set['success'] =1;
 	else : 
		$set['downloads'][] = 'downloads fail';
		$set['success'] =0;
	endif;
		$val = json_encode($set);  
		echo $val; 
 endif;
 
 ?>