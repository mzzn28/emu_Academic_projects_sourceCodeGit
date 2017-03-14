
% Syntax: plotClass2D(coord,mode,color,fontsize)
%         coord - 2D matrix,
%                 3 columns, lable:x:y 
%         mode - 1: numbers (seq from 1)
%              - 2: class label only
% Example: plotClass2D(iris, 2, 8);
% Date: 10/30/2015

function my_plotlabl(coord, mode, fontsize)
  c = coord(:, 1);
  x = coord(:, 2);
  y = coord(:, 3);

  % Only upto 7 colors!!!
  colors = ['b','r','g','m','c','k','y'];
     
  % Produce plot to get axis bounds
  plot(x,y,'w.');
  v = axis;

  % Widen axes to allow room for digits
  margin = 0.05;
  xrange = v(2)-v(1);
  yrange = v(4)-v(3);

  if ( (min(x)-v(1))/xrange < margin | (v(2)-max(x))/xrange < margin)
    v(1) = v(1) - margin*xrange;
    v(2) = v(2) + margin*xrange;
  end;

  if ( (min(y)-v(3))/yrange < margin | (v(4)-max(y))/yrange < margin)
    v(3) = v(3) - margin*yrange;
    v(4) = v(4) + margin*yrange;
  end;

  deltaX = 0.011 * (v(2)-v(1));

  % Plot initial graph using corners of axes
  % clf;
  box on;
  axis(v);
  hold on;
 
  % Plot with modes
  for i = 1:length(x)
      if mode == 1
          text(x(i)+deltaX, y(i), num2str(i), ...
              'fontsize', fontsize, ...
              'color', colors(c(i)));
      elseif mode == 2
          text(x(i)-deltaX, y(i), num2str(c(i)), ...
              'fontsize', fontsize, ...
              'color', colors(c(i)));
      end
      
  end;
  hold off;

