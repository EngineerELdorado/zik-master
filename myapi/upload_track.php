<?php 

require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);


$path='../uploads/media/';
$target_path='../uploads/tracks/';
    $target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
    move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST)):
$ids = $_REQUEST['id'];
$title = $_REQUEST['title'];
$tags = $_REQUEST['tag'];
$file_name = $_REQUEST['file_name'];
$description = $_REQUEST['description'];

   $base=$_REQUEST['userpic'];
	$binary=base64_decode($base);
	header('Content-Type: bitmap; charset=utf-8');
	$time=time();
	$imgt=$time.'.jpg';
	$file = fopen("../uploads/media/".$imgt, 'wb');
	$img=fwrite($file,$binary);
	fclose($file);
	


/* Add the original filename to our target path.  
Result is "uploads/filename.extension" */
     
    //$file_name = $_FILES['uploadedfile']['name'];
    $file_size =$_FILES['uploadedfile']['size'];

    	 
    $qry =  $conn->query("insert into tracks (uid,title,description,name,tag,art,size,time,public)VALUES('$ids','$title','$description','$file_name','$tags','$imgt','$file_size',now(),'1')");
    if($qry) :
		$inf['upload track'][] = 'uploaded';
		$inf['success'] =1;
    else:
		$inf['upload track'][] = 'not uploaded';
		$inf['success'] =0;
	endif;
		$val = json_encode($inf);
		echo $val;
 endif;   
?>
