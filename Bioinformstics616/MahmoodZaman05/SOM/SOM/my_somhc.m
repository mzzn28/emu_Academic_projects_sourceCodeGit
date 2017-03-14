% my_somhc: SOM Clustering then apply hierarchical clustering, using SOM Toolbox
% Syntax: [Class mc]= my_somhc(data,{mode},{k},{msize},{disp})
%         data  : Nxp
%         mode  : linkage - 0:single (def), 1:complete, 2:average
%         k     : clusters, def 2
%         msize : SOM map size def by somtoolbox
%         disp  : show 0:none (def), 1:som
%         Class : class labels for all 
%         mc    : class labels for map units
% Example: [C mc] = my_somhc(iris,2,3,[4 3],1);
% Date: 3/18/2002
%
% Currently only euclidean dist

function [Class,mc] = my_somhc(data,mode,k,msize,disp)
if (nargin < 5)
    disp = 0;
end
if (nargin < 4)
    msize = 0;
end
if (nargin < 3)
    k = 2;
end
if (nargin < 2)
    mode = 0;
end

% Apply SOM first
tic;
sD = som_data_struct(data);
sD = som_label(sD, 'clear', 'all');
[row col] = size(sD.data);

% Create ind as labels
for i=1:row
    sD = som_label(sD, 'add', i, num2str(i));
end
sD = som_normalize(sD, 'var');
if msize == 0
    sM = som_make(sD);
else 
    sM = som_make(sD, 'msize', msize);
end
sM = som_autolabel(sM, sD);

% Apply HC on SOM maps, get their class labels
methods = {'Single','Complete','Average'};
method = methods{mode+1};
Z = som_linkage(sM, lower(method));
mc = cluster(Z, k);
mcolor = som_clustercolor(sM, mc, 'rgb1');

% Get class labels for origin data
b = som_bmus(sM, sD);
Class = mc(b);

if disp == 1
    som_show(sM,'subplots',[2 2],'umat','all','edge','on','empty','Map Labels',...
        'color',mcolor,'footnote','SOM Hierarchical Clustering');
    som_show_add('label', sM, 'subplot', 2, 'TextSize', 6);
    sM = som_label(sM, 'clear', 'all');
    mapL = prod(sM.topol.msize);
    for i=1:mapL
      sM = som_label(sM, 'add', i, num2str(i));
    end
    som_show_add('label', sM, 'subplot', 3, 'TextColor', 'w', 'TextSize', 8);
    hold on;
    subplot(2,2,4);
    my_dendrogram(Z, 50, 8);
    title(strcat(method, ' Linkage'), 'fontsize', 10);
    hold off;
end
t = toc;
fprintf(1, 'Processing time: %.3f seconds.', t);
