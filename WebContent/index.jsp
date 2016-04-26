<html>
    <title>Lines Of Action  </title>
    <style>
        #maintitle{
                background-color: black;
                color: white;
                font-family: sans-serif
            }
    </style>
    <% 
    	
    %>
    <body >
        <h1 id="maintitle">Let's Play Lines of Action</h1>
        <div>
            <label >Choose Game Format and Color</label>
            <form action="setGame" method = "POST" >
            	<select name="gameformat" >
              		<option selected = "selected" value="5">5 X 5</option>
              		<option value="6">6 X 6</option>
            	</select>
            	<select name="ply" >
              		<option selected = "selected" value="0">Black</option>
              		<option value="1">White</option>
            	</select>
            	<select name="difficulty" >
              		<option selected = "selected" value="0">Easy</option>
              		<option value="1">Novice</option>
              		<option value="2">Hard</option>
            	</select>
            	<input type="submit"/>
            </form>
        </div>
    </body>
</html>