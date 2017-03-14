function mer=actg2num(str)

    for i=1:length(str)
        char=str(i);
        if(strcmpi(char,'a'))
            mer(i)=1;
            elseif(strcmpi(char,'t'))
            mer(i)=2;
            elseif(strcmpi(char,'g'))
            mer(i)=3;
        else %as only c left
            mer(i)=4;
         end
    end
end