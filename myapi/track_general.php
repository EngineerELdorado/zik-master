<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST)) {
$path='../uploads/media/';
//$imgt='default.png';
   $id=$_REQUEST['id'];
   $title=$_REQUEST['title'];
   $description=$_REQUEST['description'];
   $tags=$_REQUEST['tags'];  
   if($_REQUEST['userpic']!=''){
   $base=$_REQUEST['userpic'];
   $query = $conn->query("select * from tracks where tracks.id = '$id'");
        $num = $query->num_rows;
        if($num > 0) {
         $row = $query->fetch_object();
         if($row->art!='default.png') { 
               unlink('../uploads/media/'.$row->art);
         	};
        $binary=base64_decode($base);
		header('Content-Type: bitmap; charset=utf-8');
		$time=time();
		$imgt=$time.'.jpg';
		$file = fopen("../uploads/media/".$imgt, 'wb');
		$img=fwrite($file,$binary);
		fclose($file);
		}
		$qry =  $conn->query("update tracks set tracks.title ='$title',tracks.description ='$description', tracks.tag = '$tags', tracks.art = '$imgt' where tracks.id = '$id'");
		$res['general'][] = 'done';
		$res['success'] =1;
		}else{
		$qry1 =  $conn->query("update tracks set tracks.title ='$title',tracks.description ='$description', tracks.tag = '$tags' where tracks.id = '$id'");
		$res['general'][] = 'done';
		$res['success'] =1;
		}
  echo json_encode($res);
  
}

?>