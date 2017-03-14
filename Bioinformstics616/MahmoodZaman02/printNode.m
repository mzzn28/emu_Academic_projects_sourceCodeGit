function node = printNode(s, level)
node = '(';
for i=1:length(s)
    if i <= level
        node = strcat(node, sprintf('%d,', s(i)));
    else
        node = strcat(node, '-,');
    end
end
    node = strcat(node, ')');
end