
%PDP
function PDP(L, iftrace)
    L= sort(L);    % make L sorted for easy implementation
    n= length(L);    %n is the length of L
    width= max(L) ;     %width is the maximum element of L
    maxInd=find(L == width, 1, 'last');
    L(maxInd)=[] ;       % delete the max
    X= [0 width];
    level=0;
    
    PLACE(L, X, width, level, iftrace);
end
                  