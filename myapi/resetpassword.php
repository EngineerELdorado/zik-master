<?php 
session_start();
require_once "up.php";
?>
<link rel='stylesheet' href='bootstrap.min.css'>
<?php 
if($_REQUEST) :
	 $sessionid = $_REQUEST['id'];
    $expire = 30*60;
    $idu = $_REQUEST['token']; echo "<br>";

	$duration = time()-$_SESSION['requesttime'];
  
  if($duration > $expire) :
    session_destroy();
    ?>
      <body>
<div class='container'>
<br>
<div class='well'>
<form action='up.php' method='POST' id='passfrm'>
<div class='form-group'>
<input type=hidden value='<?php echo $idu; ?>' id='idu'>
<input type=password name=pass placeholder='New Password' id='pass' class='form-control' required>
</div>
<br>
<div class='form-group'>
<input type=password name=cpass placeholder='Confirm Password' class='form-control' id='cpass' required>
</div>
<br>
<div class='form-group'>
<input type=submit name=sub value='Change'  class='btn btn-info' id='sub' required>
</div>
</form>
</div>
<div id='sh'></div>
</div>
</body>
  <?php 
else :
   echo "<h1>Please Resend  Email</h3>";
   endif;
else :
      	echo "<h1>Please Resend Email</h3>";
endif;
?>
<script src='jquery-2.2.1.min.js'></script>
<script>
$(document).ready(function(){
$('#passfrm').on('submit',function(e){
e.preventDefault();
url = $(this).attr('action');
sub = $('#sub').val();
idu = $('#idu').val();
pass = $('#pass').val();
cpass=$('#cpass').val();
$.ajax({
  type : 'POST',
  url  : url,
  data : {sub,idu,pass,cpass},
  beforeSend :function(rst)
  {
       $('#sh').html("<img src='wd.gif' height=125px; width=125px>");
  },
  success : function(rst)
  {
    $('#sh').html('<h3>'+rst+'</h3>'); 
  }
});
});
});
</script>